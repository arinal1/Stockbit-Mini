package com.arinal.ui.login

import android.content.res.Configuration.*
import com.arinal.R
import com.arinal.common.onTouchBrighterEffect
import com.arinal.common.onTouchDarkerEffect
import com.arinal.databinding.FragmentLoginBinding
import com.arinal.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>() {

    override val viewModel: LoginViewModel by sharedViewModel()

    override fun setLayout() = R.layout.fragment_login

    override fun observeLiveData() = Unit

    override fun initViews() {
        when (context?.resources?.configuration?.uiMode?.and(UI_MODE_NIGHT_MASK)) {
            UI_MODE_NIGHT_YES -> {
                binding.tvForgetPassword.onTouchDarkerEffect()
                binding.tvRegister.onTouchDarkerEffect()
            }
            UI_MODE_NIGHT_NO -> {
                binding.tvForgetPassword.onTouchBrighterEffect()
                binding.tvRegister.onTouchBrighterEffect()
            }
        }
    }

}
