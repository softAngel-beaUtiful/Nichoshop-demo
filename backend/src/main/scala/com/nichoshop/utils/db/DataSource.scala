package com.nichoshop.utils.db

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}

import java.util.Properties

object DataSource {

  private[this] var ds: HikariDataSource = _

  def dataSource = ds

  def create(props: Properties) = {
    val config = new HikariConfig(props)
    ds = new HikariDataSource(config)
    ds
  }

  def load(props: Properties) = {
    ds.shutdown()
    create(props)
  }

  def shutdown() = {
    ds.shutdown()
  }
}