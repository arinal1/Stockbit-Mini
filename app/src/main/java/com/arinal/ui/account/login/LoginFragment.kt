package com.arinal.ui.account.login

import android.content.Intent
import android.content.res.Configuration.*
import android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG
import android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import android.provider.Settings
import android.util.Patterns
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED
import androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.arinal.R
import com.arinal.common.Constants
import com.arinal.common.EventObserver
import com.arinal.common.onTouchBrighterEffect
import com.arinal.common.onTouchDarkerEffect
import com.arinal.common.preferences.PreferencesHelper
import com.arinal.common.preferences.PreferencesKey
import com.arinal.data.model.UserData
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
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LoginFragment : BaseFragment<FragmentLoginBinding, AccountViewModel>() {

    private val auth by lazy { Firebase.auth }
    private val callbackManager by lazy { CallbackManager.Factory.create() }
    private val db by lazy { FirebaseFirestore.getInstance() }
    private val googleSignInIntent by lazy { GoogleSignIn.getClient(requireActivity(), gso).signInIntent }
    private val gso by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }
    private val prefHelper: PreferencesHelper by inject()
    private val promptInfo by lazy {
        BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.masuk_dengan_fingerprint))
            .setSubtitle(getString(R.string.fingerprint_guide))
            .setNegativeButtonText(getString(R.string.batalkan))
            .build()
    }
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
            val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email.value ?: "").matches()
            val isPasswordValid = password.value?.length ?: 0 >= 6
            binding.tilEmail.error = if (isEmailValid) "" else getString(R.string.email_invalid)
            binding.tilPassword.error = if (isPasswordValid) "" else getString(R.string.password_invalid)
            if (isEmailValid && isPasswordValid) emailLogin(email.value ?: "", password.value ?: "")
        })
        fingerprintLogin.observe(viewLifecycleOwner, EventObserver {
            val biometric = prefHelper.getInt(PreferencesKey.HAS_BIOMETRIC)
            if (biometric != BIOMETRIC_ERROR_NONE_ENROLLED) biometricPrompt.authenticate(promptInfo)
            else showSnackBar(getString(R.string.no_fingerprint)).apply {
                setAction(getString(R.string.tambah)) { enrollFingerprint() }
            }
        })
        register.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        })
        forgetPassword.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(R.id.action_loginFragment_to_forgetPasswordFragment)
        })
        navigateToSupport.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(R.id.action_loginFragment_to_supportFragment)
        })
    }

    override fun initViews() {
        viewModel.title.value = getString(R.string.masuk)
        viewModel.showBack.value = false
        viewModel.showProgress.value = false
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
        checkBiometricSensor()
    }

    private fun checkBiometricSensor() {
        viewModel.hasFingerprint.value = when (prefHelper.getInt(PreferencesKey.HAS_BIOMETRIC)) {
            BIOMETRIC_SUCCESS,
            BIOMETRIC_ERROR_NONE_ENROLLED -> true
            else -> false
        }
    }

    private fun enrollFingerprint() {
        val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
            putExtra(
                Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                BIOMETRIC_STRONG or DEVICE_CREDENTIAL
            )
        }
        startActivityForResult(enrollIntent, Constants.REQ_CODE_FINGERPRINT)
    }

    private val biometricPrompt by lazy {
        BiometricPrompt(this, ContextCompat.getMainExecutor(context),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode !in listOf(10, 13)) {
                        val error = if (errString.toString().isNotEmpty()) errString.toString()
                        else getString(R.string.fingerprint_failed)
                        showSnackBar(error)
                    }
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    fingerprintLogin()
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constants.REQ_CODE_FINGERPRINT -> {
                val biometric = BiometricManager.from(requireContext()).canAuthenticate()
                prefHelper.setInt(PreferencesKey.HAS_BIOMETRIC, biometric)
                checkBiometricSensor()
            }
            Constants.REQ_CODE_GOOGLE -> googleLogin(data)
            else -> callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun googleLogin(intent: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val data = hashMapOf("token" to (account.idToken ?: ""))
            login(credential, Constants.GOOGLE, R.string.google_login_failed, data)
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
                    val data = hashMapOf("token" to token)
                    login(credential, Constants.FACEBOOK, R.string.facebook_login_failed, data)
                }
            }
        }
    }

    private fun emailLogin(email: String, password: String) {
        hideKeyboard()
        viewModel.showLoading(true)
        val credential = EmailAuthProvider.getCredential(email, password)
        val data = hashMapOf("email" to email, "password" to password)
        login(credential, Constants.EMAIL, R.string.email_login_failed, data)
    }

    private fun fingerprintLogin() {
        viewModel.showLoading(true)
        val onFailed: () -> Unit = {
            viewModel.showLoading(false)
            showSnackBar(getString(R.string.fingerprint_login_failed))
        }
        val id = prefHelper.getString(PreferencesKey.INSTALLATION_ID)
        db.collection("fingerprints").document(id).get()
            .addOnFailureListener { onFailed() }
            .addOnSuccessListener {
                val uid = it["uid"].toString()
                db.collection("users").document(uid).get()
                    .addOnFailureListener { onFailed() }
                    .addOnSuccessListener { user ->
                        val data = user.toObject(UserData::class.java)
                        if (data == null) onFailed()
                        else data.run {
                            val credential = when {
                                google != null -> GoogleAuthProvider.getCredential(google?.token, null)
                                facebook != null -> FacebookAuthProvider.getCredential(facebook?.token ?: "")
                                else -> EmailAuthProvider.getCredential(email?.email ?: "", email?.password ?: "")
                            }
                            val method = getMethod()
                            val errorMessage = getErrorMessage()
                            login(credential, method, errorMessage)
                        }
                    }
            }
    }

    private fun login(credential: AuthCredential, method: String, errorMessage: Int, data: HashMap<String, String>? = null) {
        viewModel.showLoading(true)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (!it.isSuccessful) showSnackBar(getString(errorMessage))
            else {
                val user = auth.currentUser
                prefHelper.setString(PreferencesKey.USER_ID, user?.uid ?: "")
                prefHelper.setString(PreferencesKey.USER_NAME, user?.displayName ?: "")
                prefHelper.setString(PreferencesKey.USER_EMAIL, user?.email ?: "")
                prefHelper.setString(PreferencesKey.USER_PROFILE_URL, user?.photoUrl.toString())
                prefHelper.setString(PreferencesKey.USER_LOGIN_METHOD, method)
                if (data == null) navigateHome()
                else saveData(user?.uid ?: "", method, data)
            }
        }
    }

    private fun saveData(uid: String, method: String, data: HashMap<String, String>) {
        db.collection("users")
            .document(uid)
            .update(method, data)
            .addOnCompleteListener { navigateHome() }
    }

    private fun navigateHome() {
        viewModel.navigateToHome()
        viewModel.clearData()
        viewModel.showLoading(false)
    }

}
