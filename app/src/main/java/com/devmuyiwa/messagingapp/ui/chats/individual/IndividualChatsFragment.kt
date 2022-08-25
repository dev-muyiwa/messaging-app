package com.devmuyiwa.messagingapp.ui.chats.individual

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.devmuyiwa.messagingapp.R
import com.devmuyiwa.messagingapp.databinding.FragmentIndividualChatsBinding

class IndividualChatsFragment : Fragment() {
    private var _binding: FragmentIndividualChatsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentIndividualChatsBinding.inflate(inflater, container, false)
        return binding.root
    }
}