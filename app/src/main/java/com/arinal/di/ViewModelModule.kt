package com.arinal.di

import com.arinal.ui.MainViewModel
import com.arinal.ui.home.HomeViewModel
import com.arinal.ui.login.LoginViewModel
import com.arinal.ui.register.RegisterViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { MainViewModel() }
    viewModel { LoginViewModel() }
    viewModel { RegisterViewModel() }
    viewModel { HomeViewModel() }
}
