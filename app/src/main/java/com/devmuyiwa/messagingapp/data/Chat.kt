package com.devmuyiwa.messagingapp.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Chat(
    val uid: String,
    val name: String,
    val phoneNumber: String,
    val profileImage: String,
//    var lastMessage: String = ""
): Parcelable {
    constructor() : this("", "", "", "" )
}
