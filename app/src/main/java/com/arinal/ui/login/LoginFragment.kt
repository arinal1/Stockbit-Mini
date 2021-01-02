package com.arinal.ui.login

import android.content.Intent
import android.content.res.Configuration.*
import androidx.navigation.fragment.findNavController
import com.arinal.R
import com.arinal.common.Constants
import com.arinal.common.onTouchBrighterEffect
import com.arinal.common.onTouchDarkerEffect
import com.arinal.common.preferences.PreferencesHelper
import com.arinal.common.preferences.PreferencesKey
import com.arinal.databinding.FragmentLoginBinding
import com.arinal.ui.base.BaseFragment
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>() {

    private val auth by lazy { Firebase.auth }
    private val callbackManager by lazy { CallbackManager.Factory.create() }
    private val prefHelper: PreferencesHelper by inject()
    override val viewModel: LoginViewModel by sharedViewModel()

    override fun setLayout() = R.layout.fragment_login

    override fun observeLiveData() {
        with(viewModel) {
            googleLogin.observe(viewLifecycleOwner, {

            })
            facebookLogin.observe(viewLifecycleOwner, { binding.facebookLogin.callOnClick() })
            emailLogin.observe(viewLifecycleOwner, {

            })
            fingerprintLogin.observe(viewLifecycleOwner, {

            })
            register.observe(viewLifecycleOwner, {

            })
            forgetPassword.observe(viewLifecycleOwner, {

            })
            goToHelp.observe(viewLifecycleOwner, {

            })
        }
    }

    override fun initViews() {
        when (context?.resources?.configuration?.uiMode?.and(UI_MODE_NIGHT_MASK)) {
            UI_MODE_NIGHT_YES -> {
                binding.tvForgetPassword.onTouchDarkerEffect()
                binding.tvRegister.onTouchDarkerEffect()
            }
            UI_MODE_NIGHT_NO -> {
                binding.tvForgetPassword.onTouchBrighterEffect()
                binding.tvRegister.onTouchBrighterEffect()
            }
        }
        binding.facebookLogin.setPermissions("email")
        binding.facebookLogin.fragment = this
        binding.facebookLogin.registerCallback(callbackManager, facebookCallback)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private val facebookCallback by lazy {
        object : FacebookCallback<LoginResult?> {
            override fun onCancel() = Unit
            override fun onError(error: FacebookException?) {
                showSnackBar(error?.message ?: getString(R.string.facebook_login_failed))
            }

            override fun onSuccess(result: LoginResult?) {
                val token = result?.accessToken?.token
                if (token != null) {
                    val credential = FacebookAuthProvider.getCredential(token)
                    auth.signInWithCredential(credential).addOnCompleteListener {
                        if (it.isSuccessful) onSuccessLogin(Constants.FACEBOOK)
                        else showSnackBar(getString(R.string.facebook_login_failed))
                    }
                }
            }
        }
    }

    private fun onSuccessLogin(method: String) {
        val user = auth.currentUser
        prefHelper.setString(PreferencesKey.USER_ID, user?.uid ?: "")
        prefHelper.setString(PreferencesKey.USER_NAME, user?.displayName ?: "")
        prefHelper.setString(PreferencesKey.USER_EMAIL, user?.email ?: "")
        prefHelper.setString(PreferencesKey.USER_LOGIN_METHOD, method)
        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
    }

}
