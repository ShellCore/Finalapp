package mx.shellcore.android.finalapp.ui.main.ui.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_rate.view.*
import mx.shellcore.android.finalapp.R
import mx.shellcore.android.finalapp.models.NewRateEvent
import mx.shellcore.android.finalapp.models.Rate
import mx.shellcore.android.finalapp.ui.dialogs.RateDialog
import mx.shellcore.android.finalapp.ui.rates.adapters.RatesAdapter
import mx.shellcore.android.finalapp.utils.RxBus
import mx.shellcore.android.finalapp.utils.showMessage
import java.util.*
import java.util.EventListener
import kotlin.collections.ArrayList

class RatesFragment : Fragment() {

    private lateinit var _view: View

    private lateinit var ratesAdapter: RatesAdapter
    private val rateList: ArrayList<Rate> = ArrayList()

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser

    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var ratesDatabaseReference: CollectionReference

    private var ratesSubscription: ListenerRegistration? = null
    private lateinit var rateBusListener: Disposable

    private lateinit var scrollListener: RecyclerView.OnScrollListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _view = inflater.inflate(R.layout.fragment_rate, container, false)

        setupRatesDatabase()
        setupCurrentUser()

        setupRecyclerView()
        setupFab()

        subscribeToRatings()
        subscribeToNewRatings()

        return _view
    }

    override fun onDestroyView() {
        rateBusListener.dispose()
        ratesSubscription?.remove()
        _view.recView.removeOnScrollListener(scrollListener)
        super.onDestroyView()
    }

    private fun setupRatesDatabase() {
        ratesDatabaseReference = store.collection("rates")
    }

    private fun setupCurrentUser() {
        currentUser = mAuth.currentUser!!
    }

    private fun setupRecyclerView() {
        ratesAdapter = RatesAdapter(rateList)

        scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                if (dy > 0 || dy < 0 && _view.fabRating.isShown) {
                    _view.fabRating.hide()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    _view.fabRating.show()
                }
                super.onScrollStateChanged(recyclerView, newState)
            }
        }

        _view.recView.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = ratesAdapter
            addOnScrollListener(scrollListener)
        }
    }

    private fun setupFab() {
        _view.fabRating.setOnClickListener {
            RateDialog().show(fragmentManager, "")
        }
    }

    private fun saveRate(rate: Rate) {
        val newRating = HashMap<String, Any>()
        newRating["userId"] = rate.userId
        newRating["text"] = rate.text
        newRating["rate"] = rate.rate
        newRating["createdAt"] = rate.createdAt
        newRating["profileImageUrl"] = rate.profileImageUrl

        ratesDatabaseReference.add(newRating)
                .addOnCompleteListener {
                    activity!!.showMessage("Rating added!")
                }
                .addOnFailureListener {
                    activity!!.showMessage("Rating error, try again!")
                }
    }

    private fun subscribeToRatings() {
         ratesSubscription = ratesDatabaseReference.orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener(object: EventListener, com.google.firebase.firestore.EventListener<QuerySnapshot> {
                    override fun onEvent(snapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) {
                        exception?.let {
                            activity!!.showMessage("Exception!")
                            return
                        }

                        snapshot?.let {
                            rateList.clear()
                            val rates = it.toObjects(Rate::class.java)
                            rateList.addAll(rates)
                            removeFabIfRated(hasUserRated(rateList))
                            ratesAdapter.notifyDataSetChanged()
                            _view.recView.smoothScrollToPosition(0)
                        }
                    }
                })
    }

    private fun subscribeToNewRatings() {
        rateBusListener = RxBus.listen(NewRateEvent::class.java)
                .subscribe {
                    saveRate(it.rate)
                }
    }

    private fun removeFabIfRated(rated: Boolean) {
        if (rated) {
            _view.fabRating.hide()
            _view.recView.removeOnScrollListener(scrollListener)
        }
    }

    private fun hasUserRated(rates: ArrayList<Rate>) : Boolean {
        rates.forEach {
            if (it.userId == currentUser.uid) {
                return true
            }
        }
        return false
    }
}
