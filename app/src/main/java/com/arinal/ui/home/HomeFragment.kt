package com.arinal.ui.home

import com.arinal.R
import com.arinal.databinding.FragmentHomeBinding
import com.arinal.ui.MainViewModel
import com.arinal.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class HomeFragment : BaseFragment<FragmentHomeBinding, MainViewModel>() {

    override val viewModel: MainViewModel by sharedViewModel()

    override fun setLayout() = R.layout.fragment_home

    override fun observeLiveData() = Unit

}
