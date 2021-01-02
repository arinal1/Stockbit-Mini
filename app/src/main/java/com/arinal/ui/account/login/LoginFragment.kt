package com.arinal.ui.account.login

import android.content.Intent
import android.content.res.Configuration.*
import androidx.navigation.fragment.findNavController
import com.arinal.R
import com.arinal.common.Constants
import com.arinal.common.EventObserver
import com.arinal.common.onTouchBrighterEffect
import com.arinal.common.onTouchDarkerEffect
import com.arinal.common.preferences.PreferencesHelper
import com.arinal.common.preferences.PreferencesKey
import com.arinal.databinding.FragmentLoginBinding
import com.arinal.ui.account.AccountViewModel
import com.arinal.ui.base.BaseFragment
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LoginFragment : BaseFragment<FragmentLoginBinding, AccountViewModel>() {

    private val auth by lazy { Firebase.auth }
    private val callbackManager by lazy { CallbackManager.Factory.create() }
    private val googleSignInIntent by lazy { GoogleSignIn.getClient(requireActivity(), gso).signInIntent }
    private val gso by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }
    private val prefHelper: PreferencesHelper by inject()
    override val viewModel: AccountViewModel by sharedViewModel()

    override fun setLayout() = R.layout.fragment_login

    override fun observeLiveData() = with(viewModel) {
        googleLogin.observe(viewLifecycleOwner, EventObserver {
            startActivityForResult(googleSignInIntent, Constants.REQ_CODE_GOOGLE)
        })
        facebookLogin.observe(viewLifecycleOwner, EventObserver {
            binding.facebookLogin.callOnClick()
        })
        emailLogin.observe(viewLifecycleOwner, EventObserver {

        })
        fingerprintLogin.observe(viewLifecycleOwner, EventObserver {

        })
        register.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        })
        forgetPassword.observe(viewLifecycleOwner, EventObserver {

        })
    }

    override fun initViews() {
        viewModel.title.value = getString(R.string.masuk)
        viewModel.isOnRegister.value = false
        viewModel.setProgress(0)
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
        when (requestCode) {
            Constants.REQ_CODE_GOOGLE -> googleLogin(data)
            else -> callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun googleLogin(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            login(credential, Constants.GOOGLE, R.string.google_login_failed)
        } catch (e: ApiException) {
            showSnackBar(getString(R.string.google_login_failed))
        }
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
                    login(credential, Constants.FACEBOOK, R.string.facebook_login_failed)
                }
            }
        }
    }

    private fun login(credential: AuthCredential, method: String, errorMessage: Int) {
        viewModel.showLoading(true)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (!it.isSuccessful) showSnackBar(getString(errorMessage))
            else {
                val user = auth.currentUser
                prefHelper.setString(PreferencesKey.USER_ID, user?.uid ?: "")
                prefHelper.setString(PreferencesKey.USER_NAME, user?.displayName ?: "")
                prefHelper.setString(PreferencesKey.USER_EMAIL, user?.email ?: "")
                prefHelper.setString(PreferencesKey.USER_LOGIN_METHOD, method)
                viewModel.navigateToHome()
            }
            viewModel.showLoading(false)
        }
    }

}
