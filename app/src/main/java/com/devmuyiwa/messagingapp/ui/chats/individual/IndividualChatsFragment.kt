@file:Suppress("DEPRECATION")

package com.devmuyiwa.messagingapp.ui.chats.individual

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.devmuyiwa.messagingapp.data.Message
import com.devmuyiwa.messagingapp.databinding.FragmentIndividualChatsBinding
import com.devmuyiwa.messagingapp.ui.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

private val TAG = IndividualChatsFragment::class.java.simpleName

class IndividualChatsFragment : Fragment() {
    private var _binding: FragmentIndividualChatsBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<IndividualChatsFragmentArgs>()
    private var mDatabase: FirebaseDatabase? = null
    private var mUser: FirebaseUser? = null
    private var recipientUid: String? = null
    private var senderUid: String? = null
    private var messages: ArrayList<Message> = ArrayList()
    private var messagesAdapter: MessagesAdapter? = null
    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDatabase = FirebaseDatabase.getInstance()
        mUser = FirebaseAuth.getInstance().currentUser
        senderUid = mUser?.uid
        recipientUid = args.currentRecipient.uid
        dialog = ProgressDialog(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentIndividualChatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).supportActionBar?.apply {
            title = args.currentRecipient.name
            subtitle = "online"
        }

//        binding.messageInput.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                if (TextUtils.isEmpty(messageInput)) {
//                    binding.sendBtn.apply { isClickable = false; isEnabled = false }
//                } else {
//                    binding.sendBtn.apply { isClickable = true; isEnabled = true }
//                }
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//                TODO("Not yet implemented")
//            }
//
//        })
        binding.sendBtn.setOnClickListener {
            val messageInput = binding.messageInput.text.toString().trim()
            sendMessage(messageInput)
            binding.messageInput.text.clear()
        }
        loadMessages()
        readChats()
    }

    private fun Date.toLong(format: String): Long {
        val formatter = SimpleDateFormat(format)
        return formatter.format(this).toLong()
    }

    private fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }

    private fun sendMessage(message: String) {
        val date = Calendar.getInstance().time
        val dateInLong = date.time
        val messages = Message(message,
            senderUid!!,
            recipientUid!!,
            dateInLong)
        mDatabase?.reference!!.child("Chats").push()
            .setValue(messages).addOnSuccessListener {
                Log.d(TAG, "Message sent successfully")
            }.addOnFailureListener { e ->
                Log.e(TAG, "Sending failed", e)
            }
        val messageSentRef = mDatabase?.getReference("MessageList")!!
            .child(senderUid!!).child(recipientUid!!)
        messageSentRef.child("last_message").setValue(message)
        val messageReceivedRef = mDatabase?.getReference("MessageList")!!
            .child(recipientUid!!).child(senderUid!!)
        messageReceivedRef.child("last_message").setValue(message)
    }

    private fun loadMessages() {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        layoutManager.stackFromEnd = true
        binding.messageRecyclerView.layoutManager = layoutManager
    }

    private fun readChats() {
        try {
            val reference = mDatabase?.reference
            reference?.child("Chats")?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messages.clear()
                    for (dataSS in snapshot.children) {
                        val message = dataSS.getValue(Message::class.java)
                        if (message?.senderId.equals(senderUid) && message?.receiverId.equals(recipientUid)){
                            messages.add(message!!)
                        }
                        if(messagesAdapter!=null) messagesAdapter?.notifyDataSetChanged()
                        else{
                            messagesAdapter = MessagesAdapter(messages)
                            binding.messageRecyclerView.adapter = messagesAdapter
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, error.message, error.toException())
                }

            })

        } catch (e: Exception) {
           Log.e(TAG, e.message, e)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}