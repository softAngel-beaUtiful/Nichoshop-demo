package com.nichoshop.exceptions

class UserNotFoundException(message: String = "USER_NOT_FOUND") extends Exception(message)

class AuthenticationException(message: String) extends Exception(message)

class NotAuthorizedException(message: String = "NOT_AUTHORIZED") extends AuthenticationException(message)

class AuthSessionExpiredOrNotExistsException(message: String) extends AuthenticationException(message)

class TwoFactorAuthenticationException(message: String) extends AuthenticationException(message)
