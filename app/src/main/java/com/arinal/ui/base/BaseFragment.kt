package com.arinal.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.arinal.BR

abstract class BaseFragment<V : ViewDataBinding, VM : ViewModel> : Fragment() {

    protected lateinit var binding: V
    protected abstract val viewModel: VM

    @LayoutRes
    abstract fun setLayout(): Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, setLayout(), container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.setVariable(BR.viewModel, viewModel)
        initViews()
        observeLiveData()
        return binding.root
    }

    open fun initViews() = Unit
    abstract fun observeLiveData()

}
