package com.arinal.ui.account.forgetpassword

import android.util.Patterns.EMAIL_ADDRESS
import androidx.navigation.fragment.findNavController
import com.arinal.R
import com.arinal.common.EventObserver
import com.arinal.databinding.FragmentForgetPasswordBinding
import com.arinal.ui.account.AccountViewModel
import com.arinal.ui.base.BaseFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ForgetPasswordFragment : BaseFragment<FragmentForgetPasswordBinding, AccountViewModel>() {

    private val auth by lazy { Firebase.auth }
    override val viewModel: AccountViewModel by sharedViewModel()

    override fun setLayout() = R.layout.fragment_forget_password

    override fun observeLiveData() {
        viewModel.navigateNext.observe(viewLifecycleOwner, EventObserver {
            hideKeyboard()
            val isEmailValid = EMAIL_ADDRESS.matcher(viewModel.email.value ?: "").matches()
            if (!isEmailValid) binding.tilEmail.error = getString(R.string.email_invalid)
            else {
                binding.tilEmail.error = ""
                resetPassword()
            }
        })
        viewModel.navigateToSupport.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(R.id.action_forgetPasswordFragment_to_supportFragment)
        })
    }

    private fun resetPassword() {
        viewModel.showLoading(true)
        auth.sendPasswordResetEmail(viewModel.email.value ?: "")
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    viewModel.showLoading(false)
                    showSnackBar(getString(R.string.reset_password_success))
                    findNavController().navigateUp()
                }
            }
    }

    override fun initViews() {
        viewModel.title.value = getString(R.string.lupa_password)
        viewModel.showBack.value = true
    }

}
