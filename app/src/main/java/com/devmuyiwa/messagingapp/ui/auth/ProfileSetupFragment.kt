@file:Suppress("DEPRECATION")

package com.devmuyiwa.messagingapp.ui.auth

import android.app.ProgressDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.devmuyiwa.messagingapp.R
import com.devmuyiwa.messagingapp.data.User
import com.devmuyiwa.messagingapp.databinding.FragmentProfileSetupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage

private const val TAG = "ProfileSetupFragment"

class ProfileSetupFragment : Fragment() {
    private var _binding: FragmentProfileSetupBinding? = null
    private val binding get() = _binding!!
    private var mAuth: FirebaseAuth? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mStorage: FirebaseStorage? = null
    private var mFireStore: FirebaseFirestore? = null
    private var dialog: ProgressDialog? = null
    private var latestTmpUri: Uri? = null
    private val pickImageFromGalleryResult = registerForActivityResult(ActivityResultContracts
        .GetContent()) { uri: Uri? ->
        uri?.let { binding.userImageEntry.setImageURI(uri) }
        latestTmpUri = uri
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileSetupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mDatabase = FirebaseDatabase.getInstance()
        mStorage = FirebaseStorage.getInstance()
        mFireStore = FirebaseFirestore.getInstance()
        dialog = ProgressDialog(requireContext())
        dialog?.setCancelable(true)
        mAuth = FirebaseAuth.getInstance()
        pickImage()
        updateProfile()
    }

    private fun pickImage() {
        binding.userImageEntry.setOnClickListener { pickImageFromGallery() }
    }

    private fun updateProfile() {
        binding.updateBtn.setOnClickListener {
            if (binding.usernameEntry.text.toString().isEmpty()) {
                binding.usernameEntry.error = "Field cannot be empty"
            } else {
                addImageToStorage()
            }
        }
    }

    private fun pickImageFromGallery() = pickImageFromGalleryResult.launch("image/*")

    private fun addImageToStorage() {
        if (latestTmpUri != null) {
            val reference = mStorage?.reference?.child("Profile")?.child(mAuth?.currentUser!!.uid)
            reference?.putFile(latestTmpUri!!)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    reference.downloadUrl.addOnSuccessListener {
                        // it is not been added to it yet
                        updateToDatabase(it)
                        Log.d(TAG, "imageUpload: $it")
                    }.addOnFailureListener {
                        Log.e(TAG, "downloadUrl: failed", it)
                    }
                } else {
                    Toast.makeText(requireContext(), "task failed", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(requireContext(), " Image not yet selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateToDatabase(downloadUrl: Uri) {
        dialog?.setMessage("Updating profile")
        dialog?.show()
        val user = mAuth?.currentUser
        if (user != null) {
            val users = User(
                user.uid,
                binding.usernameEntry.text.toString(),
                user.phoneNumber!!,
                "$downloadUrl"
            )
            mFireStore?.collection("Users")!!.document(user.uid)
                .set(users, SetOptions.merge())
                .addOnSuccessListener {
                    dialog?.dismiss()
                    Toast.makeText(requireContext(), "Updated successfully", Toast.LENGTH_SHORT)
                        .show()
                    findNavController().navigate(R.id.allChatsFragment)
                }.addOnFailureListener { e ->
                    dialog?.dismiss()
                    Log.e(TAG, "updateToDatabase: failed", e)
                    Toast.makeText(requireContext(), "Update failed", Toast.LENGTH_SHORT).show()
                }
        } else {
            dialog?.dismiss()
            Toast.makeText(requireContext(), "You are not logged in", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}