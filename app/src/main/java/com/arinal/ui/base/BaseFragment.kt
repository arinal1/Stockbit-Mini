package com.arinal.ui.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.arinal.BR
import com.google.android.material.snackbar.Snackbar

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

    fun showSnackBar(message: String) = Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).apply { show() }

    fun hideKeyboard(): Boolean {
        val view = activity?.currentFocus
        return if (view == null) false
        else {
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

}
