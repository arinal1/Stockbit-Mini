package com.arinal.ui.account.register

import com.arinal.R
import com.arinal.databinding.FragmentRegisterBinding
import com.arinal.ui.account.AccountViewModel
import com.arinal.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class RegisterFragment : BaseFragment<FragmentRegisterBinding, AccountViewModel>() {

    override val viewModel: AccountViewModel by sharedViewModel()

    override fun setLayout() = R.layout.fragment_register

    override fun observeLiveData() = Unit

    override fun initViews() {
        viewModel.title.value = getString(R.string.daftar)
        viewModel.isBackVisible.value = true
    }

}
