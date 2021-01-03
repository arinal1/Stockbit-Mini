package com.arinal.ui.splash

import androidx.biometric.BiometricManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.arinal.R
import com.arinal.common.preferences.PreferencesHelper
import com.arinal.common.preferences.PreferencesKey
import com.arinal.common.preferences.PreferencesKey.USER_ID
import com.arinal.databinding.FragmentSplashBinding
import com.arinal.ui.MainViewModel
import com.arinal.ui.base.BaseFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SplashFragment : BaseFragment<FragmentSplashBinding, MainViewModel>() {

    private val prefHelper: PreferencesHelper by inject()
    override val viewModel: MainViewModel by sharedViewModel()
    override fun setLayout() = R.layout.fragment_splash
    override fun observeLiveData() = Unit

    override fun initViews() {
        lifecycleScope.launch {
            val biometric = BiometricManager.from(requireContext()).canAuthenticate()
            prefHelper.setInt(PreferencesKey.HAS_BIOMETRIC, biometric)
            delay(1500)
            findNavController().navigate(
                if (prefHelper.getString(USER_ID) == "") R.id.action_splashFragment_to_accountFragment
                else R.id.action_splashFragment_to_homeFragment
            )
        }
    }

}
