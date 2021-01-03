package com.arinal.ui.home

import androidx.appcompat.app.AlertDialog
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.arinal.R
import com.arinal.common.Constants
import com.arinal.common.preferences.PreferencesHelper
import com.arinal.common.preferences.PreferencesKey
import com.arinal.databinding.FragmentHomeBinding
import com.arinal.ui.base.BaseFragment
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {

    private val db by lazy { FirebaseFirestore.getInstance() }
    private val fingerprintDialog by lazy {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.activate_fingerprint)
            .setMessage(R.string.activate_fingerprint_message)
            .setPositiveButton(getString(R.string.ya)) { dialog, _ ->
                biometricPrompt.authenticate(promptInfo)
                dialog.dismiss()
            }.setNegativeButton(getString(R.string.lain_kali)) { dialog, _ ->
                dialog.dismiss()
            }.create()
    }
    private val googleSignInClient by lazy { GoogleSignIn.getClient(requireActivity(), gso) }
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
    override val viewModel: HomeViewModel by sharedViewModel()

    override fun setLayout() = R.layout.fragment_home

    override fun observeLiveData() {
        binding.btnLogout.setOnClickListener { logout() }
    }

    override fun initViews() {
        checkFingerprint()
    }

    private fun checkFingerprint() {
        val biometric = prefHelper.getInt(PreferencesKey.HAS_BIOMETRIC)
        if (biometric == BiometricManager.BIOMETRIC_SUCCESS) {
            val uid = prefHelper.getString(PreferencesKey.USER_ID)
            db.collection("users")
                .document(uid).get()
                .addOnSuccessListener {
                    val activated = it["fingerprint"].toString().removePrefix("{activated=").removeSuffix("}").toBoolean()
                    if (!activated) fingerprintDialog.show()
                }
        }
    }

    private fun activateFingerprint() {
        val id = prefHelper.getString(PreferencesKey.INSTALLATION_ID)
        val uid = prefHelper.getString(PreferencesKey.USER_ID)
        val data = hashMapOf("uid" to uid)
        db.collection("fingerprints").document(id).get().addOnSuccessListener {
            val lastUid = it["uid"].toString()
            db.collection("users").document(lastUid)
                .update("fingerprint", hashMapOf("activated" to false))
                .addOnSuccessListener {
                    db.collection("fingerprints").document(id).set(data)
                        .addOnSuccessListener {
                            db.collection("users").document(uid).update("fingerprint", hashMapOf("activated" to true))
                        }.addOnFailureListener {
                            showSnackBar(getString(R.string.activate_fingerprint_failed))
                        }
                }
        }
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
                    activateFingerprint()
                }
            })
    }

    private fun logout() {
        Firebase.auth.signOut()
        prefHelper.clearPreference(PreferencesKey.USER_ID)
        prefHelper.clearPreference(PreferencesKey.USER_NAME)
        prefHelper.clearPreference(PreferencesKey.USER_EMAIL)
        when (prefHelper.getString(PreferencesKey.USER_LOGIN_METHOD)) {
            Constants.FACEBOOK -> LoginManager.getInstance().logOut()
            Constants.GOOGLE -> {
                googleSignInClient.signOut()
                googleSignInClient.revokeAccess()
            }
        }
        prefHelper.clearPreference(PreferencesKey.USER_LOGIN_METHOD)
        findNavController().navigate(R.id.action_homeFragment_to_accountFragment)
    }

}
