package com.pebbles.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AnimationUtils
import com.pebbles.R
import com.pebbles.core.DatabaseHelper
import com.pebbles.core.Repo
import com.pebbles.core.awaitTransitionComplete
import com.pebbles.data.Device
import com.pebbles.ui.adapters.CommonListAdapter
import com.pebbles.ui.adapters.ChatDataHolder
import com.pebbles.ui.adapters.FriendDataHolder
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.peoples_list_layout.*
import kotlinx.coroutines.*


class ChatFragment : BaseFragment(), CommonListAdapter.ListInteractionsListener {

    private lateinit var peoplesListAdapter: CommonListAdapter
    private var isPeoplesLayoutVisible: Boolean = false
    private var job: Job? = null
    private var columnCount = 1

    private val chatList = arrayListOf<Any>()
    private val peoplesArrayList = arrayListOf<Any>()
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
        addChatFabButton.setOnClickListener {
            if(job?.isActive != true) {
                if (isPeoplesLayoutVisible) {
                    closePeopleList()
                } else {
                    openPeopleList()
                }
            }
        }



        peoplesList.layoutManager = LinearLayoutManager(context)
        peoplesListAdapter = CommonListAdapter(peoplesArrayList, object: CommonListAdapter.ListInteractionsListener{
            override fun onDeviceSwitchClicked(device: Device) {}

            override fun onAddDeviceClicked() {}

            override fun onDeviceAddShortcutClicked(device: Device) {}

            override fun onGraphDataDateSelected(day: String, month: String, year: String) { }

        })
        peoplesList.adapter = peoplesListAdapter
        peoplesListAdapter.notifyDataSetChanged()


    }

    private fun reloadPeoplesList() {
        DatabaseHelper.returnUsers({
            if(it) {
                peoplesArrayList.clear()
                Repo.users.forEach { friend ->
                    peoplesArrayList.add(FriendDataHolder(friend))
                }
                Repo.users.forEach { friend ->
                    peoplesArrayList.add(FriendDataHolder(friend))
                }
                Repo.users.forEach { friend ->
                    peoplesArrayList.add(FriendDataHolder(friend))
                }

                val controller =
                    AnimationUtils.loadLayoutAnimation(context, R.anim.layout_fall_down);
                peoplesList.layoutAnimation = controller;
                peoplesListAdapter.notifyDataSetChanged()
                peoplesList.scheduleLayoutAnimation()
            } else {
                //todo empty condition
            }

        }) {
            //todo error
        }
    }

    private fun openPeopleList() {
        job?.cancel()
        job = GlobalScope.launch {
            withContext(Dispatchers.Main) {
                chatMotionLayout.setTransition(R.id.bringUpFabButton)
                chatMotionLayout.transitionToEnd()
                chatMotionLayout.awaitTransitionComplete(R.id.fabButtonUp)

                chatMotionLayout.setTransition(R.id.expandPeopleListHorizontal)
                chatMotionLayout.transitionToEnd()
                chatMotionLayout.awaitTransitionComplete(R.id.peopleListHorizontalExpanded)

                chatMotionLayout.setTransition(R.id.expandPeopleListWhole)
                chatMotionLayout.transitionToEnd()
                chatMotionLayout.awaitTransitionComplete(R.id.peopleListFullExpanded)
                isPeoplesLayoutVisible = true
                reloadPeoplesList()
            }
        }

    }


    private fun closePeopleList() {
       job = GlobalScope.launch {
            withContext(Dispatchers.Main) {
                chatMotionLayout.setTransition(R.id.collapsePeopleListVertical)
                chatMotionLayout.transitionToEnd()
                chatMotionLayout.awaitTransitionComplete(R.id.peopleListHorizontalExpanded)

                chatMotionLayout.setTransition(R.id.collapsePeopleListWhole)
                chatMotionLayout.transitionToEnd()
                chatMotionLayout.awaitTransitionComplete(R.id.peopleListCollapsed)

                chatMotionLayout.setTransition(R.id.bringDownFabButton)
                chatMotionLayout.transitionToEnd()
                isPeoplesLayoutVisible = false
                peoplesArrayList.clear()
                peoplesListAdapter.notifyDataSetChanged()
            }
        }
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