package com.devmuyiwa.messagingapp.ui.auth

import androidx.lifecycle.*

class SharedLoginViewModel: ViewModel() {
    private var _phoneNumber: MutableLiveData<String> = MutableLiveData()
    val phoneNumber: LiveData<String> = _phoneNumber
    fun storePhoneNumber(phoneNo: String){
        _phoneNumber.value = phoneNo
    }
}