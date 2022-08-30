package com.devmuyiwa.messagingapp.ui.chats.all

import android.view.*
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.devmuyiwa.messagingapp.R
import com.devmuyiwa.messagingapp.data.Chat
import com.devmuyiwa.messagingapp.databinding.LayoutAllChatsItemBinding

class ChatsAdapter(var chatList: ArrayList<Chat>) :
    RecyclerView.Adapter<ChatsAdapter.ItemViewHolder>() {
    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: LayoutAllChatsItemBinding = LayoutAllChatsItemBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_all_chats_item,
            parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val chat = chatList[position]
        holder.binding.apply {
            contactName.text = chat.name
            contactImage.load(chat.profileImage) { placeholder(R.drawable.dummy) }
        }
    }

    override fun getItemCount(): Int = chatList.size

}