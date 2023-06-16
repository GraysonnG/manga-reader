package com.blanktheevil.mangareader.data

sealed class Result<T> {
    class Success<T>(val data: T): Result<T>()
    class Error<T>(val error: Throwable): Result<T>()
}
