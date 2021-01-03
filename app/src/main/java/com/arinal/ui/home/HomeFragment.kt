package com.arinal.ui.home

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
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {

    private val googleSignInClient by lazy { GoogleSignIn.getClient(requireActivity(), gso) }
    private val gso by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }
    private val prefHelper: PreferencesHelper by inject()
    override val viewModel: HomeViewModel by sharedViewModel()

    override fun setLayout() = R.layout.fragment_home

    override fun observeLiveData() {
        binding.btnLogout.setOnClickListener { logout() }
    }

    private fun logout() {
        Firebase.auth.signOut()
        when (prefHelper.getString(PreferencesKey.USER_LOGIN_METHOD)) {
            Constants.FACEBOOK -> LoginManager.getInstance().logOut()
            Constants.GOOGLE -> {
                googleSignInClient.signOut()
                googleSignInClient.revokeAccess()
            }
        }
        prefHelper.clearAllPreferences()
        findNavController().navigate(R.id.action_homeFragment_to_accountFragment)
    }

}
