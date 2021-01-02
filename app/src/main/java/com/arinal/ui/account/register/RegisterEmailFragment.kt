package com.arinal.ui.account.register

import androidx.navigation.fragment.findNavController
import com.arinal.R
import com.arinal.common.EventObserver
import com.arinal.databinding.FragmentRegisterEmailBinding
import com.arinal.ui.account.AccountViewModel
import com.arinal.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class RegisterEmailFragment : BaseFragment<FragmentRegisterEmailBinding, AccountViewModel>() {

    override val viewModel: AccountViewModel by sharedViewModel()

    override fun setLayout() = R.layout.fragment_register_email

    override fun observeLiveData() {
        viewModel.navigateNext.observe(viewLifecycleOwner, EventObserver{
            findNavController().navigate(R.id.action_registerEmailFragment_to_registerNameFragment)
        })
    }

    override fun initViews() {
        viewModel.title.value = getString(R.string.daftar)
        viewModel.isOnRegister.value = true
        viewModel.setProgress(33)
    }

}
