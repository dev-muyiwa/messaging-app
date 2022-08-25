package com.devmuyiwa.messagingapp.ui.chats.all

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.devmuyiwa.messagingapp.R
import com.devmuyiwa.messagingapp.databinding.FragmentAllChatsBinding
import com.devmuyiwa.messagingapp.ui.MainActivity
import com.google.firebase.auth.FirebaseAuth

class AllChatsFragment : Fragment() {
    private var _binding: FragmentAllChatsBinding? = null
    private val binding get() = _binding!!
    private var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
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
        if (mAuth!!.currentUser == null) {
            binding.apply { toggleVisibility(notLoggedInView, loggedInView) }
            binding.loginBtn.setOnClickListener{
                findNavController().navigate(R.id.loginFragment)
            }
        } else {
            binding.apply { toggleVisibility(loggedInView, notLoggedInView) }
        }
    }

    private fun toggleVisibility(toVisible: View, toInvisible: View) {
        toVisible.visibility = View.VISIBLE
        toInvisible.visibility = View.INVISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}