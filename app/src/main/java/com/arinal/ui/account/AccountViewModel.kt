package com.arinal.ui.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arinal.common.Event
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AccountViewModel : ViewModel() {

    private var job: Job? = null

    val title = MutableLiveData("")
    val isOnRegister = MutableLiveData(false)
    val progress = MutableLiveData(0f)
    fun setProgress(progress: Int) {
        val currentProgress = ((this.progress.value ?: .33f) * 100).toInt()
        val range = if (progress < currentProgress) currentProgress downTo progress else currentProgress..progress
        viewModelScope.launch {
            if (job != null) job?.cancel()
            job = launch {
                for (i in range) {
                    this@AccountViewModel.progress.value = i / 100f
                    delay(3)
                }
            }
        }
    }

    val email = MutableLiveData("")
    val password = MutableLiveData("")
    val name = MutableLiveData("")
    val isLoading = MutableLiveData(false)
    fun showLoading(show: Boolean) = isLoading.postValue(show)

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

    private val _navigateNext = MutableLiveData<Event<Unit>>()
    val navigateNext: LiveData<Event<Unit>> get() = _navigateNext
    fun navigateNext() = _navigateNext.postValue(Event(Unit))

    private val _navigateBack = MutableLiveData<Event<Unit>>()
    val navigateBack: LiveData<Event<Unit>> get() = _navigateBack
    fun navigateBack() = _navigateBack.postValue(Event(Unit))

    private val _navigateToHelp = MutableLiveData<Event<Unit>>()
    val navigateToHelp: LiveData<Event<Unit>> get() = _navigateToHelp
    fun navigateToHelp() = _navigateToHelp.postValue(Event(Unit))

    private val _navigateToHome = MutableLiveData<Event<Unit>>()
    val navigateToHome: LiveData<Event<Unit>> get() = _navigateToHome
    fun navigateToHome() = _navigateToHome.postValue(Event(Unit))

}
