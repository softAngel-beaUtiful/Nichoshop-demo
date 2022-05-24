package com.nichoshop.utils.auth

import java.security.{MessageDigest, SecureRandom}

object SecureUtils {
  val TOKEN_LENGTH = 45 // TOKEN_LENGTH is not the return size from a hash,
  // but the total characters used as random token prior to hash
  // 45 was selected because System.nanoTime().toString returns
  // 19 characters. 45 + 19 = 64. Therefore we are guaranteed
  // at least 64 characters (bytes) to use in hash, to avoid MD5 collision < 64

  val TOKEN_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_.-"
  val secureRandom = new SecureRandom()

  private def toHex(bytes: Array[Byte]): String = bytes.map("%02x".format(_)).mkString("")

  private def sha(s: String): String = {
    toHex(MessageDigest.getInstance("SHA-256").digest(s.getBytes("UTF-8")))
  }

  private def md5(s: String): String = {
    toHex(MessageDigest.getInstance("MD5").digest(s.getBytes("UTF-8")))
  }

  private def generateToken(tokenLength: Int): String = {
    val l = TOKEN_CHARS.length()

    def loop(acc: String, i: Int): String = {
      if (i == 0) acc
      else loop(acc + TOKEN_CHARS(secureRandom.nextInt(l)), i - 1)
    }
    loop("", tokenLength)
  }

  /*
  * Hash the Token to return a 32 or 64 character HEX String
  *
  * Parameters:
  * tokenprefix: string to concatenate with random generated token prior to HASH to improve uniqueness, such as username
  *
  * Returns:
  * MD5 hash of (username + current time + random token generator) as token, 128 bits, 32 characters
  * or
  * SHA-256 hash of (username + current time + random token generator) as token, 256 bits, 64 characters
  */
  def generateMD5Token(tokenprefix: String): String = {
    md5(tokenprefix + System.nanoTime() + generateToken(TOKEN_LENGTH))
  }

  def generateSHAToken(tokenprefix: String): String = {
    sha(tokenprefix + System.nanoTime() + generateToken(TOKEN_LENGTH))
  }

  def generateSMSCode(): String = {        
    (100000 + secureRandom.nextInt(900000)).toString()    
  }
}