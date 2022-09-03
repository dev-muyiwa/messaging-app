package com.devmuyiwa.messagingapp.data

data class Message(
    val message: String,
    val senderId: String,
    val receiverId: String,
    val timeStamp: Long,
) {
    constructor() : this("","","", 0)
}
