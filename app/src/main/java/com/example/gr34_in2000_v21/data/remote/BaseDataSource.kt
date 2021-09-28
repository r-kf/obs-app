package com.example.gr34_in2000_v21.data.remote

import com.example.gr34_in2000_v21.data.models.DataResult
import com.example.gr34_in2000_v21.utils.Messages
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Benytt deg av denne abstrakte klassen for å opprette nye DataSource klasser
 */

open class BaseDataSource {

    protected suspend fun <T> getResult(request: suspend () -> Response<T>): DataResult<T> {
        try {
            val response = request()
            return if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    DataResult.success(body)
                } else {
                    DataResult.error(Messages.Message(0, "Server response error"))
                }
            } else {
                DataResult.error(Messages.Message(response.code(), response.message()))
            }
        } catch (e: Exception) {
            val errorMessage = e.message ?: e.toString()
            return when (e) {
                is SocketTimeoutException -> {
                    DataResult.error(Messages.Message(408, "Timed out!"))
                }
                is ConnectException -> {
                    DataResult.error(Messages.Message(111, "Check your internet connection!"))
                }
                is UnknownHostException -> {
                    DataResult.error(Messages.Message(111, "Check your internet connection!"))
                }
                else -> {
                    DataResult.error(Messages.Message(0, errorMessage))
                }
            }
        }
    }

    /*
    old code.
    protected suspend fun <T> getResult(call: suspend () -> Response<T>): Resource<T> {
        try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Log.d("REAL METALERTS SHIT", body.toString())
                    return Resource.success(body)
                }
            }
            return error(" ${response.code()} ${response.message()}")
        } catch (e: Exception) {
            return error(e.message ?: e.toString())
        }
    }

    private fun <T> error(message: String): Resource<T> {
        Timber.e(message)
        return Resource.error("Network call has failed for a following reason: $message")
    }
     */

}