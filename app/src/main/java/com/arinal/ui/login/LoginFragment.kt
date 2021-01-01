package com.arinal.ui.login

import com.arinal.R
import com.arinal.databinding.FragmentLoginBinding
import com.arinal.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>() {

    override val viewModel: LoginViewModel by sharedViewModel()

    override fun setLayout() = R.layout.fragment_login

    override fun observeLiveData() = Unit

}
