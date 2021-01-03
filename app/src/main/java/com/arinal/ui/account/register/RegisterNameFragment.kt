package com.arinal.ui.account.register

import androidx.navigation.fragment.findNavController
import com.arinal.R
import com.arinal.common.EventObserver
import com.arinal.databinding.FragmentRegisterNameBinding
import com.arinal.ui.account.AccountViewModel
import com.arinal.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class RegisterNameFragment : BaseFragment<FragmentRegisterNameBinding, AccountViewModel>() {

    override val viewModel: AccountViewModel by sharedViewModel()

    override fun setLayout() = R.layout.fragment_register_name

    override fun observeLiveData() {
        viewModel.navigateNext.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(R.id.action_registerNameFragment_to_registerPasswordFragment)
        })
        viewModel.navigateToSupport.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(R.id.action_registerNameFragment_to_supportFragment)
        })
    }

    override fun initViews() {
        viewModel.showProgress.value = true
        viewModel.setProgress(66)
    }

}
