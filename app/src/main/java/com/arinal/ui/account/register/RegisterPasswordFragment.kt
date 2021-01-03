package com.arinal.ui.account.register

import androidx.navigation.fragment.findNavController
import com.arinal.R
import com.arinal.common.Constants
import com.arinal.common.EventObserver
import com.arinal.common.preferences.PreferencesHelper
import com.arinal.common.preferences.PreferencesKey
import com.arinal.databinding.FragmentRegisterPasswordBinding
import com.arinal.ui.account.AccountViewModel
import com.arinal.ui.base.BaseFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class RegisterPasswordFragment : BaseFragment<FragmentRegisterPasswordBinding, AccountViewModel>() {

    private val auth by lazy { Firebase.auth }
    private val prefHelper: PreferencesHelper by inject()
    override val viewModel: AccountViewModel by sharedViewModel()

    override fun setLayout() = R.layout.fragment_register_password

    override fun observeLiveData() = with(viewModel) {
        viewModel.navigateToSupport.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(R.id.action_registerPasswordFragment_to_supportFragment)
        })
        navigateNext.observe(viewLifecycleOwner, EventObserver {
            hideKeyboard()
            if (password.value?.length ?: 0 < 6) binding.tilPassword.error = getString(R.string.password_invalid)
            else {
                binding.tilPassword.error = ""
                viewModel.showLoading(true)
                auth.createUserWithEmailAndPassword(email.value ?: "", password.value ?: "")
                    .addOnCompleteListener { task ->
                        if (task.exception != null) showSnackBar(task.exception?.message ?: getString(R.string.register_failed))
                        else {
                            val user = auth.currentUser
                            user?.updateProfile(userProfileChangeRequest { displayName = name.value ?: "" })
                            prefHelper.setString(PreferencesKey.USER_ID, user?.uid ?: "")
                            prefHelper.setString(PreferencesKey.USER_NAME, name.value ?: "")
                            prefHelper.setString(PreferencesKey.USER_EMAIL, user?.email ?: "")
                            prefHelper.setString(PreferencesKey.USER_LOGIN_METHOD, Constants.EMAIL)
                            prefHelper.setString(PreferencesKey.USER_PROFILE_URL, user?.photoUrl.toString())
                            viewModel.navigateToHome()
                            viewModel.clearData()
                        }
                        viewModel.showLoading(false)
                    }
            }
        })
    }

    override fun initViews() {
        viewModel.showProgress.value = true
        viewModel.setProgress(100)
        viewModel.password.value = ""
    }

}
