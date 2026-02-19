package com.example.data.config

import com.typesafe.config.ConfigFactory

object AppConfig {
    private val config = ConfigFactory.load()

    val dbUrl = config.getString("db.url")
    val dbUser = config.getString("db.user")
    val dbPassword = config.getString("db.password")

    val jwtSecret = config.getString("jwt.secret")
    val jwtIssuer = config.getString("jwt.issuer")
    val jwtValidity = config.getLong("jwt.validity")

    val redisUrl = config.getString("redis.url")

    val rabbitMqHost = config.getString("rabbitmq.host")
    val rabbitMqPort = config.getInt("rabbitmq.port")
}