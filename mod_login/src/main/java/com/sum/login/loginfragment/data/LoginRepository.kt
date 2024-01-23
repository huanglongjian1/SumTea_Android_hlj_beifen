package com.sum.login.loginfragment.data

import com.sum.common.model.User
import com.sum.common.provider.UserServiceProvider
import com.sum.network.response.BaseResponse

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(val dataSource: LoginDataSource) {

    // in-memory cache of the loggedInUser object
    var user: BaseResponse<User?>? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

    suspend fun logout() {
        user = null
        UserServiceProvider.clearUserInfo()
        dataSource.logout()
    }

    suspend fun login(username: String, password: String): Result<BaseResponse<User?>> {
        // handle login
        val result = dataSource.login(username, password)

        if (result is Result.Success) {
            setLoggedInUser(result.data)
        }

        return result
    }

    private fun setLoggedInUser(loggedInUser: BaseResponse<User?>?) {
        this.user = loggedInUser
        UserServiceProvider.saveUserInfo(loggedInUser?.data)
        UserServiceProvider.saveUserPhone(loggedInUser?.data?.username)
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }
}