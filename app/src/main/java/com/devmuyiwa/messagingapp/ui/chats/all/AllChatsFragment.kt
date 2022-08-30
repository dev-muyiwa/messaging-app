package com.devmuyiwa.messagingapp.ui.chats.all

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.devmuyiwa.messagingapp.R
import com.devmuyiwa.messagingapp.data.Chat
import com.devmuyiwa.messagingapp.databinding.FragmentAllChatsBinding
import com.devmuyiwa.messagingapp.ui.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

private val TAG = AllChatsFragment::class.java.simpleName

class AllChatsFragment : Fragment() {
    private var _binding: FragmentAllChatsBinding? = null
    private val binding get() = _binding!!
    private var mAuth: FirebaseAuth? = null
    private var mFirestore: FirebaseFirestore? = null
    private val chats: ArrayList<Chat> = ArrayList()
    private var chatsAdapter: ChatsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        mFirestore = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAllChatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).supportActionBar?.title = "Chats"
        inflateToolbarMenu()
        validateUserSignIn()

    }

    override fun onResume() {
        super.onResume()
        if (mAuth?.currentUser != null) {
            mFirestore?.collection("Presence")!!.document(mAuth?.currentUser!!.uid)
                .set("presence" to "online", SetOptions.merge())
        }
    }

    override fun onPause() {
        super.onPause()
        if (mAuth?.currentUser != null) {
            mFirestore?.collection("Presence")!!.document(mAuth?.currentUser!!.uid)
                .set("presence" to "offline", SetOptions.merge())
        }
    }

    private fun inflateToolbarMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.toolbar_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.sort -> {
                        Toast.makeText(requireContext(), "Filters not yet implemented", Toast
                            .LENGTH_SHORT).show()
                        true
                    }
                    R.id.profile -> {
                        Toast.makeText(requireContext(), "Profile not yet implemented", Toast
                            .LENGTH_SHORT).show()
                        true
                    }
                    R.id.settings -> {
                        Toast.makeText(requireContext(), "Settings not yet implemented", Toast
                            .LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun validateUserSignIn() {
        if (mAuth!!.currentUser == null) {
            binding.apply { toggleVisibility(notLoggedInView, loggedInView) }
            binding.loginBtn.setOnClickListener {
                findNavController().navigate(R.id.loginFragment)
            }
        } else {
            binding.apply { toggleVisibility(loggedInView, notLoggedInView) }
            launchUsersScreen()
        }
    }

    private fun launchUsersScreen() {
        chatsAdapter = ChatsAdapter(chats)
        mFirestore?.collection("Users")!!.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val user = document.toObject(Chat::class.java)
                    if (user.uid != mAuth?.currentUser!!.uid) chats.add(user)
                }
                chatsAdapter!!.notifyDataSetChanged()
                Log.d(TAG, "$chats")
            }
            .addOnFailureListener {
                Log.e(TAG, it.message, it)
            }
        binding.allChatsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,
                false)
            adapter = chatsAdapter
        }
    }

    private fun toggleVisibility(toVisible: View, toInvisible: View) {
        toVisible.visibility = View.VISIBLE
        toInvisible.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}