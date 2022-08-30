package com.devmuyiwa.messagingapp.ui.auth

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.devmuyiwa.messagingapp.R
import com.devmuyiwa.messagingapp.databinding.FragmentLoginBinding

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
            val pattern = "^0?(70|8([01])|9([01]))\\d{8}$"
            val phoneNumberSuffix = binding.phoneNumberEntry.text.trim()
            if (Regex(pattern).containsMatchIn(phoneNumberSuffix)){
                phoneNo = "${binding.countryCode.selectedCountryCodeWithPlus}$phoneNumberSuffix"
                mSharedViewModel.storePhoneNumber(phoneNo)
                findNavController().navigate(R.id.action_loginFragment_to_loginValidationFragment)
            } else {
                Toast.makeText(requireContext(), "Invalid Phone Number", Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}