package com.arinal.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arinal.common.Constants
import com.arinal.common.Event
import com.arinal.data.Api
import com.arinal.data.model.WatchListModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(private val api: Api) : ViewModel() {

    val name = MutableLiveData("")
    val email = MutableLiveData("")
    val profileUrl = MutableLiveData("")
    fun setProfile(name: String, email: String, profileUrl: String) {
        this.name.value = name
        this.email.value = email
        this.profileUrl.value = profileUrl
    }

    private var page = 0

    private val _watchList = MutableLiveData(mutableListOf<WatchListModel.Data>())
    val watchList: LiveData<MutableList<WatchListModel.Data>> get() = _watchList

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData("")
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _openBurger = MutableLiveData<Event<Unit>>()
    val openBurger: LiveData<Event<Unit>> get() = _openBurger
    fun openBurger() = _openBurger.postValue(Event(Unit))

    private val _logout = MutableLiveData<Event<Unit>>()
    val logout: LiveData<Event<Unit>> get() = _logout
    fun logout() = _logout.postValue(Event(Unit))

    fun initData() {
        if (_watchList.value.isNullOrEmpty()) getWatchList()
    }

    fun getWatchList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _errorMessage.postValue("")
                _isLoading.postValue(true)
                val data = api.getWatchlist(++page)
                val watchList = mutableListOf<WatchListModel.Data>().apply {
                    addAll(_watchList.value ?: listOf())
                    addAll(data.data)
                }
                _watchList.postValue(watchList)
                if (watchList.isEmpty()) _errorMessage.postValue(Constants.DATA_EMPTY)
            } catch (t: Throwable) {
                _errorMessage.postValue(t.message)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun clearData() {
        _watchList.value = mutableListOf()
        page = 0
    }

}
