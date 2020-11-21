package com.pebbles.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.pebbles.R
import com.pebbles.core.DatabaseHelper
import com.pebbles.core.Repo
import com.pebbles.core.awaitTransitionComplete
import com.pebbles.data.Device
import com.pebbles.ui.adapters.CommonListAdapter
import com.pebbles.ui.adapters.ChatDataHolder
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ChatFragment : Fragment(), CommonListAdapter.ListInteractionsListener {

    private var columnCount = 1

    private val chatList = arrayListOf<Any>()
    private lateinit var chatListAdapter: CommonListAdapter
    private var listener: OnChatTabInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnChatTabInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnChatTabInteractionListener")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        val listView = view.findViewById<RecyclerView>(R.id.chatRecyclerView)
        // Set the adapter
        if (listView is RecyclerView) {
            with(listView) {
                layoutManager = LinearLayoutManager(context)
                chatListAdapter = CommonListAdapter(chatList, this@ChatFragment)
                adapter = chatListAdapter
            }

        }
        Log.d("Pebbles_debug", "Chat Fragment on create view")
        return view
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int): Fragment {
            Log.d("Pebbles_debug", "Chat Fragment on new instance")
            return ChatFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("Pebbles_debug", "Chat Fragment on view created")
        loadChats()
        initAddChat()
    }

    private fun initAddChat() {

        chatMotionLayout.addTransitionListener(object: MotionLayout.TransitionListener {
            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {

            }

            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
            }

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                if(p1 == R.id.bringUpFabButton) {
                    chatMotionLayout.setTransition(R.id.expandPeopleListHorizontal)
                    chatMotionLayout.transitionToEnd()
                }
            }

        })


        addChatFabButton.setOnClickListener {
            if (chatMotionLayout.currentState == R.id.fabButtonUp) {
                closePeopleList()
            } else {
                openPeopleList()
            }
        }

    }

    private fun openPeopleList() {
        chatMotionLayout.setTransition(R.id.bringUpFabButton)
        chatMotionLayout.transitionToEnd()
    }


    private fun closePeopleList() {
        chatMotionLayout.setTransition(R.id.bringDownFabButton)
        chatMotionLayout.transitionToEnd()
    }

    private fun loadChats() {
        Repo.user?.deviceSetId?.let {
            DatabaseHelper.returnChatsForUid(it, {
                Repo.chats.let { availableChats ->
                    if(availableChats.isNotEmpty()) {
                        chatList.clear()
                        availableChats.forEach { user ->
                            chatList.add(ChatDataHolder(user))
                        }
                        chatListAdapter.notifyDataSetChanged()
                    } else {

                    }
                }
            }, {
                //error handel
            })
        }
    }


    interface OnChatTabInteractionListener {
        fun onChatClicked()
    }

    override fun onDeviceSwitchClicked(device: Device) {}

    override fun onAddDeviceClicked() {}

    override fun onDeviceAddShortcutClicked(device: Device) {}

    override fun onGraphDataDateSelected(day: String, month: String, year: String) {}
}