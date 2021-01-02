package com.arinal.ui.account.register

import com.arinal.R
import com.arinal.common.EventObserver
import com.arinal.databinding.FragmentRegisterPasswordBinding
import com.arinal.ui.account.AccountViewModel
import com.arinal.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class RegisterPasswordFragment : BaseFragment<FragmentRegisterPasswordBinding, AccountViewModel>() {

    override val viewModel: AccountViewModel by sharedViewModel()

    override fun setLayout() = R.layout.fragment_register_password

    override fun observeLiveData() {
        viewModel.navigateNext.observe(viewLifecycleOwner, EventObserver{
            showSnackBar("GOOD!")
        })
    }

    override fun initViews() {
        viewModel.setProgress(100)
    }

}
