package com.arinal.ui.account

import androidx.navigation.fragment.findNavController
import com.arinal.R
import com.arinal.common.EventObserver
import com.arinal.databinding.FragmentAccountBinding
import com.arinal.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AccountFragment : BaseFragment<FragmentAccountBinding, AccountViewModel>() {

    override val viewModel: AccountViewModel by sharedViewModel()

    override fun setLayout() = R.layout.fragment_account

    override fun observeLiveData() {
        viewModel.navigateToHome.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(R.id.action_accountFragment_to_homeFragment)
        })
        viewModel.navigateBack.observe(viewLifecycleOwner, EventObserver { activity?.onBackPressed() })
        viewModel.navigateToHelp.observe(viewLifecycleOwner, EventObserver {

        })
    }

}
