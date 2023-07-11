package com.blanktheevil.mangareader.data

sealed class Result<T> {
    class Success<T>(val data: T) : Result<T>()
    class Error<T>(val error: Throwable) : Result<T>()

    fun onSuccess(onSuccess: (T) -> Unit): Result<T> {
        if (this is Success) {
            onSuccess(data)
        }
        return this
    }

    fun onError(onError: (Throwable) -> Unit): Result<T> {
        if (this is Error) {
            onError(error)
        }
        return this
    }

    fun collectOrDefault(default: T): T {
        return if (this is Success) {
            data
        } else {
            default
        }
    }

    fun collectOrNull(): T? {
        return if (this is Success) {
            data
        } else {
            null
        }
    }
}
