@file:Suppress("DEPRECATION")

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
import com.devmuyiwa.messagingapp.data.User
import com.devmuyiwa.messagingapp.databinding.FragmentLoginValidationBinding
import com.google.firebase.*
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit

private const val TAG = "LoginValidationFragment"

class LoginValidationFragment : Fragment() {
    private var _binding: FragmentLoginValidationBinding? = null
    private val binding get() = _binding!!
    private var mAuth: FirebaseAuth? = null
    private var mFireStore: FirebaseFirestore? = null
    private var dialog: ProgressDialog? = null
    private val mSharedViewModel: SharedLoginViewModel by activityViewModels()
    private var mVerificationId: String? = null
    private var mResendToken: PhoneAuthProvider.ForceResendingToken? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(requireContext())
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLoginValidationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        mFireStore = FirebaseFirestore.getInstance()
        dialog = ProgressDialog(requireActivity())
        dialog?.setCancelable(false)
        mSharedViewModel.phoneNumber.observe(viewLifecycleOwner) { phoneNum ->
            startCredentialsVerification(phoneNum)
        }
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

        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.e(TAG, "onVerificationFailed:${e.message}", e)
            dialog?.dismiss()
            if (e is FirebaseTooManyRequestsException) {
                Toast.makeText(requireContext(),
                    "${e.message}",
                    Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Verification failed", Toast.LENGTH_SHORT).show()
            }
            binding.resendCode.visibility = View.VISIBLE
            binding.resendCode.setOnClickListener {
                mSharedViewModel.phoneNumber.observe(viewLifecycleOwner) { phoneNum ->
                    startCredentialsVerification(phoneNum)
                }
            }
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
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        dialog?.setMessage("Signing In")
        dialog?.show()
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user = task.result.user
                    addUserToDatabase(user!!)
                    dialog?.dismiss()
                } else {
                    Log.e(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(requireContext(), "Sign In Failed", Toast.LENGTH_SHORT)
                        .show()
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Log.d(TAG, "verifyWithCode:invalid code")
                    }
                    dialog?.dismiss()
                }
            }
    }

    private fun addUserToDatabase(user: FirebaseUser) {
        val users = User(
            user.uid,
            "",
            user.phoneNumber!!,
            "",
        )
        mFireStore?.collection("Users")!!.document(user.uid)
            .set(users).addOnSuccessListener {
                Log.d(TAG, "addUserToDatabase: success")
                findNavController().navigate(R.id.action_loginValidationFragment_to_profileSetupFragment)
            }.addOnFailureListener { e ->
                Log.e(TAG, "addUserToDatabase: failed", e)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}