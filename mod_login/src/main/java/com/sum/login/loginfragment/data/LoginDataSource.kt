package com.sum.login.loginfragment.data

import com.sum.common.model.User
import com.sum.network.manager.ApiManager
import com.sum.network.response.BaseResponse
import kotlinx.coroutines.delay
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    suspend fun login(username: String, password: String): Result<BaseResponse<User?>> {
        try {
            // TODO: handle loggedInUser authentication
            // val fakeUser = LoggedInUser(java.util.UUID.randomUUID().toString(), "Jane Doe")
            val response = ApiManager.api.login(username, password)
            delay(2000)
            if (response?.isFailed()!!) {
                return Result.Error(IOException(response.errorMsg))
            }
            return Result.Success(response)
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }

//        val response = requestFlow(errorBlock = { code, msg ->
//            Loge.e("错误:" + code + ":" + msg)
//        }, requestCall = {
//            ApiManager.api.login(username, password)
//        })
//        return Result.Success(response)
    }

    suspend fun logout() {
        // TODO: revoke authentication
        val response = ApiManager.api.logout()
    }
}