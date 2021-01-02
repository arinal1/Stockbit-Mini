package com.arinal.di

import com.arinal.ui.MainViewModel
import com.arinal.ui.home.HomeViewModel
import com.arinal.ui.account.AccountViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { MainViewModel() }
    viewModel { AccountViewModel() }
    viewModel { HomeViewModel() }
}
