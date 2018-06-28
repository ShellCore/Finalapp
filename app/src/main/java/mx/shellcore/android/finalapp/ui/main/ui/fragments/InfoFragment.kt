package mx.shellcore.android.finalapp.ui.main.ui.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.squareup.picasso.Picasso
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_info.view.*
import mx.shellcore.android.finalapp.R
import mx.shellcore.android.finalapp.models.TotalMessagesEvent
import mx.shellcore.android.finalapp.utils.CirclerTransform
import mx.shellcore.android.finalapp.utils.RxBus
import mx.shellcore.android.finalapp.utils.showMessage
import java.util.EventListener

class InfoFragment : Fragment() {

    private lateinit var _view : View

    private val mAuth : FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser : FirebaseUser

    private val store : FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var chatDatabaseReference : CollectionReference

    private var chatSubscription : ListenerRegistration? = null
    private lateinit var infoBusListener: Disposable

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _view = inflater.inflate(R.layout.fragment_info, container, false)

        setupDatabase()
        setupCurrentUser()
        setupCurrentUserInfoUI()

        // FirebaseStyle
//        subscribeToTotalMessagesFirebaseStyle()
        subscribetoTotalMessagesEventBusReactiveStyle()

        return _view
    }

    override fun onDestroyView() {
        infoBusListener.dispose()
        chatSubscription?.remove()
        super.onDestroyView()
    }

    private fun setupDatabase() {
        chatDatabaseReference = store.collection("chat")
    }

    private fun setupCurrentUser() {
        currentUser = mAuth.currentUser!!
    }

    private fun setupCurrentUserInfoUI() {
        _view.txtInfoEmail.text = currentUser.email
        _view.txtInfoName.text = currentUser.displayName?.let {
            it
        } ?: run {
            getString(R.string.info_message_no_name)
        }
        currentUser.photoUrl?.let {
            Picasso.get()
                    .load(currentUser.photoUrl)
                    .resize(200, 200)
                    .centerCrop()
                    .transform(CirclerTransform())
                    .into(_view.imgProfile)
        } ?: kotlin.run {
            Picasso.get()
                    .load(R.drawable.ic_person)
                    .resize(200, 200)
                    .centerCrop()
                    .transform(CirclerTransform())
                    .into(_view.imgProfile)
        }
    }

    private fun subscribeToTotalMessagesFirebaseStyle() {
        chatDatabaseReference.addSnapshotListener(object: EventListener, com.google.firebase.firestore.EventListener<QuerySnapshot> {

            override fun onEvent(querySnapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) {
                exception?.let {
                    activity!!.showMessage("Exception!")
                    return
                }
                querySnapshot?.let {
                    _view.txtTotalMessages.text = "${it.size()}"
                }
            }
        })
    }

    private fun subscribetoTotalMessagesEventBusReactiveStyle() {
        infoBusListener = RxBus.listen(TotalMessagesEvent::class.java)
                .subscribe {
                    _view.txtTotalMessages.text = "${it.total}"
                }
    }
}
