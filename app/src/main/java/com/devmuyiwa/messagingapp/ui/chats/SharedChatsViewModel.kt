package com.devmuyiwa.messagingapp.ui.chats

import androidx.lifecycle.*

class SharedChatsViewModel: ViewModel() {
    private var _recipientId: MutableLiveData<String> = MutableLiveData()
    private var _recipientName: MutableLiveData<String> = MutableLiveData()
    val recipientId: LiveData<String> = _recipientId
    val recipientName: LiveData<String> = _recipientName

    fun storeUserInfo(uid: String, name: String){
        _recipientId.value = uid
        _recipientName.value = name
    }
}