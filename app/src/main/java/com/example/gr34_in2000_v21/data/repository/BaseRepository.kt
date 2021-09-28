package com.example.gr34_in2000_v21.data.repository

import androidx.lifecycle.liveData
import com.example.gr34_in2000_v21.data.models.DataResult

open class BaseRepository {

    protected fun <T> makeRequest(request: suspend () -> DataResult<T>) = liveData<DataResult<T>> {
        emit(DataResult.loading())

        val response = request.invoke()

        when (response.status) {
            DataResult.Status.SUCCESS -> {
                emit(DataResult.success(response.data!!))
            }
            DataResult.Status.ERROR -> {
                emit(DataResult.error(response.message!!))
            }
            else -> {
            }
        }
    }

    /*protected fun <T, A> makeRequestAndSave(
        databaseQuery: () -> LiveData<T>,
        networkCall: suspend () -> DataResult<A>,
        saveCallResult: suspend (A) -> Unit
    ): LiveData<DataResult<T>> =
        liveData(Dispatchers.IO) {
            emit(DataResult.loading())
            val source = databaseQuery.invoke().map {
                Timber.d("makeRequestAndSave: $it")
                DataResult.success(it)
            }
            emitSource(source)

            val response = networkCall.invoke()
            when (response.status) {
                DataResult.Status.SUCCESS -> {
                    saveCallResult(response.data!!)
                }
                DataResult.Status.ERROR -> {
                    emit(DataResult.error(response.message!!))
                    emitSource(source)
                }
                else -> {
                }
            }
        }*/
}