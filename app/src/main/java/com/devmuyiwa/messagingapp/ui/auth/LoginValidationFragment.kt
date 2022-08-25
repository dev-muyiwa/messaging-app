package com.devmuyiwa.messagingapp.ui.auth

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.devmuyiwa.messagingapp.R
import com.devmuyiwa.messagingapp.databinding.FragmentLoginValidationBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

private const val TAG = "LoginValidationFragment"

class LoginValidationFragment : Fragment() {
    private var _binding: FragmentLoginValidationBinding? = null
    private val binding get() = _binding!!
    private var mAuth: FirebaseAuth? = null
    private var dialog: ProgressDialog? = null
    private val mSharedViewModel: SharedLoginViewModel by activityViewModels()
    private var mVerificationId: String? = null
    private var mResendToken: PhoneAuthProvider.ForceResendingToken? = null
    private lateinit var phoneNo: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLoginValidationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mSharedViewModel.phoneNumber.observe(viewLifecycleOwner) { phoneNumber ->
            phoneNo = phoneNumber
        }
        mAuth = FirebaseAuth.getInstance()
        dialog = ProgressDialog(requireActivity())
        startCredentialsVerification(phoneNo)
        binding.loginBtn.setOnClickListener {
            val smsCode = binding.otpCode.text.toString()
            verifyCredentials(mVerificationId!!, smsCode)
        }
    }


    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d(TAG, "onVerificationCompleted:$credential")
            dialog?.dismiss()
            signInWithPhoneAuthCredential(credential)
            dialog?.setMessage("Signing In")
            dialog?.show()
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.d(TAG, "onVerificationFailed:${e.message}", e)
            dialog?.dismiss()
            Toast.makeText(requireContext(), "Invalid Phone Number", Toast.LENGTH_SHORT).show()
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            Log.d(TAG, "onCodeSent:$verificationId")
            dialog?.dismiss()
            mVerificationId = verificationId
            mResendToken = token
        }

    }

    private fun startCredentialsVerification(phoneNo: String) {
        dialog?.setMessage("Verifying : $phoneNo")
        dialog?.show()
        val options = PhoneAuthOptions.newBuilder(mAuth!!)
            .setPhoneNumber(phoneNo)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyCredentials(verificationId: String, code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        dialog?.setMessage("Signing In")
        dialog?.show()
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    dialog?.dismiss()
                    Log.d(TAG, "signInWithCredential:success")
                    findNavController().navigate(R.id.profileSetupFragment)
                } else {
                    dialog?.dismiss()
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(requireContext(), "Verification Failed", Toast.LENGTH_SHORT)
                        .show()
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Log.d(TAG, "verifyWithCode:invalid code")
                    }
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}