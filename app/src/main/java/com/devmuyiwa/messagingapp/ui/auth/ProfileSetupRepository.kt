package com.devmuyiwa.messagingapp.ui.auth

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ProfileSetupRepository(
    private val firestore: FirebaseFirestore,
    private val storageRef: StorageReference
) {
    fun addImageToFireStore(imageUri: Uri){
        val imageRef = storageRef.child("Users").child("profile")
        imageRef.putFile(imageUri).addOnCompleteListener { task ->
            if (task.isSuccessful){
                imageRef.downloadUrl.addOnSuccessListener { uri ->

                }
            }
        }
    }
}