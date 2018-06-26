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
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_chat.view.*
import mx.shellcore.android.finalapp.R
import mx.shellcore.android.finalapp.R.id.edtMessage
import mx.shellcore.android.finalapp.models.Message
import mx.shellcore.android.finalapp.ui.chat.adapters.ChatAdapter
import mx.shellcore.android.finalapp.utils.showMessage
import java.util.*
import java.util.EventListener
import kotlin.collections.HashMap

class ChatFragment : Fragment() {

    private lateinit var _view: View
    private lateinit var chatAdapter: ChatAdapter
    private var messages: ArrayList<Message> = ArrayList()

    private var store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var chatDbReference: CollectionReference

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser

    private var chatSubscription : ListenerRegistration? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _view = inflater.inflate(R.layout.fragment_chat, container, false)

        setupChatDatabase()
        setupCurrentUser()
        setupRecyclerView()
        setupChatBtn()

        subscribeToChatMessages()

        return _view
    }

    override fun onDestroy() {
        chatSubscription?.remove()
        super.onDestroy()
    }

    private fun setupChatDatabase() {
        chatDbReference = store.collection("chat")
    }

    private fun setupCurrentUser() {
        currentUser = mAuth.currentUser!!
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(messages, currentUser.uid)

        _view.recChat.layoutManager = LinearLayoutManager(context)
        _view.recChat.setHasFixedSize(true)
        _view.recChat.itemAnimator = DefaultItemAnimator()
        _view.recChat.adapter = chatAdapter

    }

    private fun setupChatBtn() {
        _view.btnSend.setOnClickListener {
            val message = edtMessage.text.toString()
            if (message.isNotEmpty()) {
                val photo = currentUser.photoUrl?.let {
                    currentUser.photoUrl.toString()
                } ?: run {
                    ""
                }
                val message = Message(currentUser.uid, message, photo, Date())
                saveMessage(message)
                _view.edtMessage.setText("")
            }
        }
    }

    private fun saveMessage(message: Message) {
        val newMessage = HashMap<String, Any>()
        newMessage["authorId"] = message.authorId
        newMessage["message"] = message.message
        newMessage["profileImageUrl"] = message.profileImageUrl
        newMessage["sentAt"] = message.sentAt

        chatDbReference.add(newMessage)
                .addOnCompleteListener {
                    activity!!.showMessage("Message added!")
                }
                .addOnFailureListener {
                    activity!!.showMessage("Error, try again!")
                }
    }

    private fun subscribeToChatMessages() {
        chatSubscription = chatDbReference
                .orderBy("sentAt", Query.Direction.DESCENDING)
                .limit(100)
                .addSnapshotListener(object : EventListener, com.google.firebase.firestore.EventListener<QuerySnapshot> {

            override fun onEvent(snapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) {
                exception?.let {
                    activity!!.showMessage("Exception!")
                    return
                }

                snapshot?.let {
                    messages.clear()
                    val list = it.toObjects(Message::class.java)
                    messages.addAll(list.asReversed())
                    chatAdapter.notifyDataSetChanged()
                    _view.recChat.smoothScrollToPosition(messages.size)
                }
            }

        })
    }
}
