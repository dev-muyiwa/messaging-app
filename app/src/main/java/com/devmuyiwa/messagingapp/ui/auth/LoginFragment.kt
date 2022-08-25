package com.devmuyiwa.messagingapp.ui.auth

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.*
import androidx.navigation.fragment.findNavController
import com.devmuyiwa.messagingapp.R
import com.devmuyiwa.messagingapp.databinding.FragmentLoginBinding
import com.devmuyiwa.messagingapp.ui.CustomDialog

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val mSharedViewModel: SharedLoginViewModel by activityViewModels()
    private lateinit var phoneNo: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.nextBtn.setOnClickListener {
            phoneNo = "+{binding.countryCode.selectedCountryCodeWithPlus}${binding
                .phoneNumberEntry}"
           mSharedViewModel.storePhoneNumber(phoneNo)
            findNavController().navigate(R.id.action_loginFragment_to_loginValidationFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}