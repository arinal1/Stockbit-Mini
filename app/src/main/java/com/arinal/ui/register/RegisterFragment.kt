package com.arinal.ui.register

import com.arinal.R
import com.arinal.databinding.FragmentRegisterBinding
import com.arinal.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class RegisterFragment : BaseFragment<FragmentRegisterBinding, RegisterViewModel>() {

    override val viewModel: RegisterViewModel by sharedViewModel()

    override fun setLayout() = R.layout.fragment_register

    override fun observeLiveData() = Unit

}
