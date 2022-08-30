package com.devmuyiwa.messagingapp.data

data class Chat(
    val uid: String,
    val name: String,
    val phoneNumber: String,
    val profileImage: String,
){
    constructor(): this("","","","")
}
