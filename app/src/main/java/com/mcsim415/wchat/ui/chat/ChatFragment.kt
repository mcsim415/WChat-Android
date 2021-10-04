package com.mcsim415.wchat.ui.chat

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mcsim415.wchat.databinding.FragmentChatBinding
import com.mcsim415.wchat.socketHandler.SocketHandler
import java.util.*
import kotlin.collections.ArrayList

class ChatFragment : Fragment() {
    private lateinit var chatViewModel: ChatViewModel
    private var _binding: FragmentChatBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        chatViewModel =
            ViewModelProvider(
                this,
                ViewModelProvider.NewInstanceFactory()
            ).get(ChatViewModel::class.java)

        _binding = FragmentChatBinding.inflate(inflater, container, false)

        val bundle = arguments
        val socketHandler = bundle!!.getSerializable("socketHandler") as SocketHandler
        val toolbar: TextView = binding.toolbarTitle
        toolbar.text = socketHandler.getAddress()

        val arrayList: ArrayList<Chat> = ArrayList()
        val me = User("", "나")
        val other = User("1", "상대방")

        val recyclerview = binding.recyclerviewMessageList
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        recyclerview.layoutManager = layoutManager
        val adapter = ChatAdapter(arrayList)
        recyclerview.adapter = adapter

        recyclerview.isNestedScrollingEnabled = true

        binding.buttonChatboxSend.setOnClickListener {
            val text = binding.edittextChatbox.text
            if (text.isNotEmpty()) {
                val random = Random()

                val user: User
                val type: Int
                if (random.nextInt(2) == 1) {
                    user = me
                    type = Chat.VIEW_TYPE_MESSAGE_SENT
                } else {
                    user = other
                    type = Chat.VIEW_TYPE_MESSAGE_RECEIVED
                }
                arrayList.add(
                    Chat(
                        text.toString(),
                        user,
                        type,
                        System.currentTimeMillis()
                    )
                )
                adapter.notifyItemInserted(arrayList.size)
                binding.edittextChatbox.text.clear()
                recyclerview.smoothScrollToPosition(arrayList.size - 1)
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}