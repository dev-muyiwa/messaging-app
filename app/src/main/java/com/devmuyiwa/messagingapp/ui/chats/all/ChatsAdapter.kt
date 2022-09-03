package com.devmuyiwa.messagingapp.ui.chats.all

import android.content.Context
import android.util.Log
import android.view.*
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.devmuyiwa.messagingapp.R
import com.devmuyiwa.messagingapp.data.Chat
import com.devmuyiwa.messagingapp.databinding.LayoutAllChatsItemBinding

class ChatsAdapter(
    private var context: Context,
    private var chatList: ArrayList<Chat>,
) :
    RecyclerView.Adapter<ChatsAdapter.ChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        Log.i("ChatsAdapter", "onCreateViewHolder: ViewHolder created")
        val view = LayoutInflater.from(context).inflate(R.layout.layout_all_chats_item,
            parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        Log.i("CityAdapter", "onBindViewHolder: position $position")
        val chat = chatList[position]
        holder.setData(chat, position)
//        holder.binding.apply {
//            contactName.text = chat.name
//            contactImage.load(chat.profileImage) { placeholder(R.drawable.dummy) }
////            lastMessage.text = chat.lastMessage
//        }
        holder.itemView.setOnClickListener {
            val action = AllChatsFragmentDirections
                .actionAllChatsFragmentToIndividualChatsFragment(chat)
            holder.itemView.findNavController().navigate(action)
        }
    }

    override fun getItemCount(): Int = chatList.size

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val binding: LayoutAllChatsItemBinding = LayoutAllChatsItemBinding.bind(itemView)

        //        Testing somethings out
        private var currentPosition: Int = -1
        private var currentChat: Chat? = null
        fun setData(chat: Chat, position: Int) {
            binding.apply {
                contactName.text = chat.name
                contactImage.load(chat.profileImage) { placeholder(R.drawable.dummy) }
            }
            this.currentPosition = position
            this.currentChat = chat
        }

    }

}