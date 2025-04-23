package com.example.normalapp.data.api.wrappers

sealed class AuthWrapperError<E> {
    class JWTInvalid<E> : AuthWrapperError<E>()
    class InnerError<E>(val innerError: E) : AuthWrapperError<E>()
}