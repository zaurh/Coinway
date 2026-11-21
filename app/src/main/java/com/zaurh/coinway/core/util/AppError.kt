package com.zaurh.coinway.core.util

import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException

/** Failures the UI knows how to explain to people. */
enum class AppError { Network, RateLimited, Server, Unknown }

class AppException(val error: AppError, cause: Throwable? = null) : Exception(error.name, cause)

fun Throwable.toAppError(): AppError = when (this) {
    is AppException -> error
    is IOException -> AppError.Network
    is HttpException -> when (code()) {
        429 -> AppError.RateLimited
        in 500..599 -> AppError.Server
        else -> AppError.Unknown
    }
    is SerializationException -> AppError.Server
    else -> AppError.Unknown
}

/** Like [runCatching], but never swallows coroutine cancellation and maps failures to [AppException]. */
inline fun <T> appRunCatching(block: () -> T): Result<T> = try {
    Result.success(block())
} catch (e: CancellationException) {
    throw e
} catch (e: Throwable) {
    Result.failure(AppException(e.toAppError(), e))
}

fun Result<*>.appErrorOrNull(): AppError? = exceptionOrNull()?.toAppError()
