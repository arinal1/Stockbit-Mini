package com.arinal.ui.splash

import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.arinal.R
import com.arinal.databinding.FragmentSplashBinding
import com.arinal.ui.MainViewModel
import com.arinal.ui.base.BaseFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SplashFragment : BaseFragment<FragmentSplashBinding, MainViewModel>() {
    override val viewModel: MainViewModel by sharedViewModel()
    override fun setLayout() = R.layout.fragment_splash
    override fun observeLiveData() = Unit
    override fun initViews() {
        lifecycleScope.launch {
            delay(1000)
            findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
        }
    }
}
