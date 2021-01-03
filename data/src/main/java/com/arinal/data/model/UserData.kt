package com.arinal.data.model

import com.arinal.common.Constants
import com.arinal.common.R

data class UserData(
    val facebook: Token? = null,
    val google: Token? = null,
    val email: Email? = null
) {
    data class Token(val token: String = "")
    data class Email(
        val email: String = "",
        val password: String = ""
    )

    fun getMethod() = when {
        google != null -> Constants.GOOGLE
        facebook != null -> Constants.FACEBOOK
        else -> Constants.EMAIL
    }

    fun getErrorMessage() = when {
        google != null -> R.string.google_login_failed
        facebook != null -> R.string.facebook_login_failed
        else -> R.string.email_login_failed
    }
}
