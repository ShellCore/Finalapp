package mx.shellcore.android.finalapp.ui.chat.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_chat_item_left.view.*
import kotlinx.android.synthetic.main.fragment_chat_item_right.view.*
import mx.shellcore.android.finalapp.R
import mx.shellcore.android.finalapp.models.Message
import mx.shellcore.android.finalapp.utils.CirclerTransform
import mx.shellcore.android.finalapp.utils.getFormattedDate
import mx.shellcore.android.finalapp.utils.inflate

class ChatAdapter(val list: List<Message>, val userId: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val GLOBAL_MESSAGE = 1
    private val MY_MESSAGE = 2
    private val layoutRight = R.layout.fragment_chat_item_right
    private val layoutLeft = R.layout.fragment_chat_item_left

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            MY_MESSAGE -> ViewHolderRight(parent.inflate(layoutRight))
            else -> ViewHolderLeft(parent.inflate(layoutLeft))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            MY_MESSAGE -> (holder as ViewHolderRight).bind(list[position])
            else -> (holder as ViewHolderLeft).bind(list[position])
        }
    }

    override fun getItemCount() = list.size

    override fun getItemViewType(position: Int) =
            if (list[position].authorId == userId) MY_MESSAGE else GLOBAL_MESSAGE

    class ViewHolderRight(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(message: Message) = with(itemView) {
            txtMessageRight.text = message.message
            txtTimeRight.text = message.sentAt.getFormattedDate()
            if (message.profileImageUrl.isNotEmpty()) {
                Picasso.get()
                        .load(message.profileImageUrl)
                        .resize(100, 100)
                        .centerCrop()
                        .transform(CirclerTransform())
                        .into(imgProfileRight)
            } else {

            }
        }
    }

    class ViewHolderLeft(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(message: Message) = with(itemView) {
            txtMessageLeft.text = message.message
            txtTimeLeft.text = message.sentAt.getFormattedDate()
            if (message.profileImageUrl.isNotEmpty()) {
                Picasso.get()
                        .load(message.profileImageUrl)
                        .resize(100, 100)
                        .centerCrop()
                        .transform(CirclerTransform())
                        .into(imgProfileLeft)
            } else {

            }
        }
    }

}