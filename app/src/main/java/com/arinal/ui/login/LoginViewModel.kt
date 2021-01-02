package com.arinal.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arinal.common.Event

class LoginViewModel : ViewModel() {

    val email = MutableLiveData("")
    val password = MutableLiveData("")
    val isLoading = MutableLiveData(false)

    private val _googleLogin = MutableLiveData<Event<Unit>>()
    val googleLogin: LiveData<Event<Unit>> get() = _googleLogin
    fun googleLogin() = _googleLogin.postValue(Event(Unit))

    private val _facebookLogin = MutableLiveData<Event<Unit>>()
    val facebookLogin: LiveData<Event<Unit>> get() = _facebookLogin
    fun facebookLogin() = _facebookLogin.postValue(Event(Unit))

    private val _emailLogin = MutableLiveData<Event<Unit>>()
    val emailLogin: LiveData<Event<Unit>> get() = _emailLogin
    fun emailLogin() = _emailLogin.postValue(Event(Unit))

    private val _fingerprintLogin = MutableLiveData<Event<Unit>>()
    val fingerprintLogin: LiveData<Event<Unit>> get() = _fingerprintLogin
    fun fingerprintLogin() = _fingerprintLogin.postValue(Event(Unit))

    private val _register = MutableLiveData<Event<Unit>>()
    val register: LiveData<Event<Unit>> get() = _register
    fun register() = _register.postValue(Event(Unit))

    private val _forgetPassword = MutableLiveData<Event<Unit>>()
    val forgetPassword: LiveData<Event<Unit>> get() = _forgetPassword
    fun forgetPassword() = _forgetPassword.postValue(Event(Unit))

    private val _goToHelp = MutableLiveData<Event<Unit>>()
    val goToHelp: LiveData<Event<Unit>> get() = _goToHelp
    fun goToHelp() = _goToHelp.postValue(Event(Unit))

}
