package mx.shellcore.android.finalapp.ui.main.ui.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
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
        _view.recView.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = ratesAdapter
        }
    }

    private fun setupFab() {
        _view.fabRating.setOnClickListener {
            RateDialog().show(fragmentManager, "")
        }
    }

    private fun saveRate(rate: Rate) {
        val newRating = HashMap<String, Any>()
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
}
