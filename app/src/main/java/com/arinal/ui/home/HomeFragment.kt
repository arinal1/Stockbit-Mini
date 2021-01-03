package com.arinal.ui.home

import androidx.appcompat.app.AlertDialog
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.arinal.BR
import com.arinal.R
import com.arinal.common.Constants
import com.arinal.common.EndlessScrollListener
import com.arinal.common.EventObserver
import com.arinal.common.preferences.PreferencesHelper
import com.arinal.common.preferences.PreferencesKey
import com.arinal.databinding.FragmentHomeBinding
import com.arinal.databinding.HeaderNavigationDrawerBinding
import com.arinal.ui.base.BaseFragment
import com.arinal.ui.home.adapter.ShimmerAdapter
import com.arinal.ui.home.adapter.WatchListAdapter
import com.bumptech.glide.Glide
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
    private val endlessScroll = object : EndlessScrollListener() {
        override fun onLoadMore() = viewModel.getWatchList()
    }
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
    private val shimmerAdapter by lazy { ShimmerAdapter(layoutInflater, 20) }
    private val watchListAdapter by lazy { WatchListAdapter(layoutInflater) {} }
    override val viewModel: HomeViewModel by sharedViewModel()

    override fun setLayout() = R.layout.fragment_home

    override fun observeLiveData() {
        viewModel.openBurger.observe(viewLifecycleOwner, EventObserver {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        })
        viewModel.logout.observe(viewLifecycleOwner, EventObserver { logout() })
        viewModel.watchList.observe(viewLifecycleOwner, { watchListAdapter.submitList(it) })
    }

    override fun initViews() {
        checkFingerprint()
        AppBarConfiguration(findNavController().graph, binding.drawerLayout)
        binding.navigationView.setupWithNavController(findNavController())
        val headerNavDrawer = DataBindingUtil.inflate<HeaderNavigationDrawerBinding>(
            layoutInflater,
            R.layout.header_navigation_drawer,
            binding.navigationView,
            true
        )
        headerNavDrawer.setVariable(BR.viewModel, viewModel)
        val profileUrl = prefHelper.getString(PreferencesKey.USER_PROFILE_URL)
        viewModel.setProfile(
            prefHelper.getString(PreferencesKey.USER_NAME),
            prefHelper.getString(PreferencesKey.USER_EMAIL),
            profileUrl
        )
        Glide.with(requireContext()).load(profileUrl).into(headerNavDrawer.ivProfile)
        binding.rvShimmer.adapter = shimmerAdapter
        binding.rvWatchlist.adapter = watchListAdapter
        binding.rvWatchlist.addOnScrollListener(endlessScroll)
        binding.swipeLayout.setOnRefreshListener {
            binding.swipeLayout.isRefreshing = false
            viewModel.clearData()
            endlessScroll.resetData()
            viewModel.getWatchList()
        }
        viewModel.initData()
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
