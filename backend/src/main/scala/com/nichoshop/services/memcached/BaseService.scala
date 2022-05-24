package com.nichoshop.services.memcached

import com.nichoshop.model.dto._
import com.nichoshop.utils.Memcached
import com.nichoshop.utils.Memcached._
import monix.execution.CancelableFuture
import org.slf4j.LoggerFactory
import shade.memcached.Codec

import scala.concurrent.duration._

/**
 * Created by Evgeny Zhoga on 06.09.15.
 */
trait BaseService {
  private val log = LoggerFactory.getLogger(getClass)

  protected def set(key: String, value: AnyRef): CancelableFuture[Unit] = {
    log.info(s"MEMCACHED[SET]: $key")

    value match {
      case v: UserDto => Memcached.client.set(key, v, 1 day)
      case v: SessionDto => Memcached.client.set(key, v, 1 day)
      case v: CartDto => Memcached.client.set(key, v, 1 day)
      case v: String => Memcached.client.set(key, v, 1 day)
    }
  }

  protected def get[T](key: String)(implicit codec: Codec[T]): Option[T] = {
    log.info(s"MEMCACHED[GET]: $key")
    Memcached.client.awaitGet[T](key)
  }

  protected def delete(key: String): CancelableFuture[Boolean] = {
    log.info(s"MEMCACHED[DELETE]: $key")
    Memcached.client.delete(key)
  }

}
