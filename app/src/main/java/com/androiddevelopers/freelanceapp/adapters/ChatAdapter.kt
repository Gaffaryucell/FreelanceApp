package com.androiddevelopers.freelanceapp.adapters

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.freelanceapp.databinding.RowChatBinding
import com.androiddevelopers.freelanceapp.model.ChatModel
import com.androiddevelopers.freelanceapp.view.chat.ChatsFragmentDirections
import com.bumptech.glide.Glide

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<ChatModel>() {
        override fun areItemsTheSame(oldItem: ChatModel, newItem: ChatModel): Boolean {
            return oldItem.chatId == newItem.chatId
        }

        override fun areContentsTheSame(oldItem: ChatModel, newItem: ChatModel): Boolean {
            return oldItem == newItem
        }
    }
    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var chatsList: List<ChatModel>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    inner class ChatViewHolder(val binding: RowChatBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = RowChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chatsList[position]
        val sharedPref = holder.itemView.context.getSharedPreferences("cht", Context.MODE_PRIVATE)

        try {
            Glide.with(holder.itemView.context).load(chat.receiverUserImage)
                .into(holder.binding.chatImage)
            val time = chat.chatLastMessageTimestamp
            if (time != null){
                holder.binding.chatLastMessageTimeStamp.text = time.substringAfter(" ").split(":").take(2).joinToString(separator = ":")
            }
            val seen = chat.seen
            if (seen != null){
                if (seen){
                    holder.binding.unreadMessageIndicator.visibility = ViewGroup.INVISIBLE
                }else{
                    holder.binding.unreadMessageIndicator.visibility = ViewGroup.VISIBLE
                }
            }
            holder.binding.apply {
                chatItem = chat
            }
            holder.itemView.setOnClickListener {
                sharedPref.edit().putString("place", "chat").apply()
                val action = ChatsFragmentDirections.actionChatsFragmentToMessagesFragment(
                    chat.chatId.toString(),
                    chat.receiverId.toString(),
                    chat.receiverUserName.toString(),
                    chat.receiverUserImage.toString()
                )
                Navigation.findNavController(it).navigate(action)
            }
        }catch (e: Exception){
            Toast.makeText(holder.itemView.context, "Hata : Chat", Toast.LENGTH_SHORT).show()
        }


    }

    override fun getItemCount(): Int {
        return chatsList.size
    }
}