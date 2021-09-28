package com.example.gr34_in2000_v21.data.models

import androidx.annotation.Keep
import com.example.gr34_in2000_v21.utils.Messages

@Keep
data class DataResult<out T>(val status: Status, val data: T?, val message: Messages.Message?) {

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    companion object {
        fun <T> success(data: T): DataResult<T> {
            return DataResult(
                Status.SUCCESS,
                data,
                null
            )
        }

        fun <T> error(message: Messages.Message, data: T? = null): DataResult<T> {
            return DataResult(
                Status.ERROR,
                data,
                message
            )
        }

        fun <T> loading(data: T? = null): DataResult<T> {
            return DataResult(
                Status.LOADING,
                data,
                null
            )
        }
    }
}
