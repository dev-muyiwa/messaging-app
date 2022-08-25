package com.devmuyiwa.messagingapp.ui.auth

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.devmuyiwa.messagingapp.R
import com.devmuyiwa.messagingapp.databinding.FragmentProfileSetupBinding

class ProfileSetupFragment : Fragment() {
    private var _binding: FragmentProfileSetupBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileSetupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}