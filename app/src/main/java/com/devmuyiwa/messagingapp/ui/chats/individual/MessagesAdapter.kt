package com.devmuyiwa.messagingapp.ui.chats.individual

import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.devmuyiwa.messagingapp.R
import com.devmuyiwa.messagingapp.data.Message
import com.devmuyiwa.messagingapp.databinding.LayoutReceivedMessageBinding
import com.devmuyiwa.messagingapp.databinding.LayoutSentMessageBinding
import com.google.firebase.auth.FirebaseAuth

class MessagesAdapter(private var messages: ArrayList<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    private val SENT_ITEM = 0
    private val RECEIVED_ITEM = 1

    inner class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: LayoutSentMessageBinding = LayoutSentMessageBinding.bind(itemView)
    }

    inner class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: LayoutReceivedMessageBinding = LayoutReceivedMessageBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == SENT_ITEM) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_sent_message,
                parent, false)
            SentMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_received_message,
                parent, false)
            ReceivedMessageViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (FirebaseAuth.getInstance().uid == message.senderId) {
            SENT_ITEM
        } else {
            RECEIVED_ITEM
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder.javaClass == SentMessageViewHolder::class.java) {
            val viewHolder = holder as SentMessageViewHolder
            viewHolder.binding.sentMessage.text = message.message
        } else {
            val viewHolder = holder as ReceivedMessageViewHolder
            viewHolder.binding.receivedMessage.text = message.message
        }
    }

    override fun getItemCount(): Int = messages.size

}