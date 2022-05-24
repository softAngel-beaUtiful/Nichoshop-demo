package com.nichoshop.services.duo

import com.duosecurity.Client
import com.duosecurity.model.Token
import com.nichoshop.Environment.duo.{apiHost, clientId, clientSecret, redirectUri}
import com.nichoshop.exceptions.{AuthSessionExpiredOrNotExistsException, TwoFactorAuthenticationException}
import com.nichoshop.services.memcached.BaseService

class DuoService extends BaseService {

  val duoClient: Client = new Client.Builder(clientId, clientSecret, apiHost, redirectUri).build

  def createAuthUrl(username: String): String = {
    duoClient.healthCheck

    val state = duoClient.generateState

    set(generateStateKey(state), username)

    duoClient.createAuthUrl(username, state)
  }

  def getEmailOrUserIdByState(state: String): Option[String] = get[String](generateStateKey(state))

  def deleteStateCacheRecord(state: String) = delete(generateStateKey(state))

  def validateAuthorizationWithPrompt(duoCode: String, state: String): Unit = {
    val stateKey = generateStateKey(state)

    get[String](stateKey) match {
      case Some(username) =>
        val token = duoClient.exchangeAuthorizationCodeFor2FAResult(duoCode, username)
        if (!authWasSuccessful(token)) throw new TwoFactorAuthenticationException("TWO_FACTOR_AUTHENTICATION_FAILED")

      case None => throw new AuthSessionExpiredOrNotExistsException("AUTHORIZATION_SESSION_EXPIRED_OR_NOT_EXISTS")
    }
  }

  private def authWasSuccessful(token: Token): Boolean = {
    token != null && token.getAuth_result != null && "ALLOW".equalsIgnoreCase(token.getAuth_result.getStatus)
  }

  private def generateStateKey(state: String) = s"duo_state__$state"

}
