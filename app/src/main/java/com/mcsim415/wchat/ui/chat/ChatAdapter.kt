package com.mcsim415.wchat.ui.chat

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.marginTop
import androidx.recyclerview.widget.RecyclerView
import com.mcsim415.wchat.R
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter(private var mMessageList: ArrayList<Chat>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View?

        if (viewType == Chat.VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_sent, parent, false)
            return SentMessageHolder(view)
        }
        view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_received, parent, false)
        return ReceivedMessageHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        val chat = mMessageList[position]

        return if (chat.getType() == Chat.VIEW_TYPE_MESSAGE_SENT) {
            Chat.VIEW_TYPE_MESSAGE_SENT
        } else {
            Chat.VIEW_TYPE_MESSAGE_RECEIVED
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = mMessageList[position]
        val lastMessage = mMessageList.getOrNull(position-1)

        when (holder.itemViewType) {
            Chat.VIEW_TYPE_MESSAGE_SENT -> {
                var timeDuplicate = false

                if (lastMessage?.getSender() === message.getSender() && lastMessage.getCreatedAt() == message.getCreatedAt()) {
                    timeDuplicate = true
                }
                (holder as SentMessageHolder).bind(message, timeDuplicate)
            }
            Chat.VIEW_TYPE_MESSAGE_RECEIVED -> {
                var nameDuplicate = false; var timeDuplicate = false

                if (lastMessage?.getSender() === message.getSender()) {
                    nameDuplicate = true
                    if (lastMessage?.getCreatedAt() == message.getCreatedAt()) {
                        timeDuplicate = true
                    }
                }
                (holder as ReceivedMessageHolder).bind(message, nameDuplicate, timeDuplicate)
            }
        }
    }

    override fun getItemCount(): Int {
        return mMessageList.size
    }

    class SentMessageHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var messageText: TextView = itemView.findViewById(R.id.text_message_outgoing)
        private var timeText: TextView = itemView.findViewById(R.id.text_message_outgoing_time)

        fun bind(message: Chat, timeDuplicate: Boolean) {
            messageText.text = message.getMessage()
            if (!timeDuplicate) {
                timeText.text = message.getCreatedAt()
            } else {
                timeText.text = ""
            }
        }
    }

    class ReceivedMessageHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var messageText: TextView = itemView.findViewById(R.id.text_message_incoming)
        private var timeText: TextView = itemView.findViewById(R.id.text_message_received_time)
        private var nameText: TextView = itemView.findViewById(R.id.text_message_name)

        fun bind(message: Chat, nameDuplicate: Boolean, timeDuplicate: Boolean) {
            if (nameDuplicate) {
                val layoutParams = (messageText.layoutParams as ViewGroup.MarginLayoutParams)
                layoutParams.setMargins(20, -50, 0, 0)
                messageText.layoutParams = layoutParams
                nameText.text = ""
            } else {
                val layoutParams = (messageText.layoutParams as ViewGroup.MarginLayoutParams)
                layoutParams.setMargins(20, 2, 0, 0)
                messageText.layoutParams = layoutParams
                nameText.text = message.getSender().getName()
            }
            messageText.text = message.getMessage()
            if (!timeDuplicate) {
                timeText.text = message.getCreatedAt()
            } else {
                timeText.text = ""
            }
        }
    }
}