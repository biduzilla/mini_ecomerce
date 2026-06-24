package com.example.ms_config

import jdk.jfr.Enabled
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.config.server.EnableConfigServer

@SpringBootApplication
@EnableConfigServer
class MsConfigApplication

fun main(args: Array<String>) {
	runApplication<MsConfigApplication>(*args)
}
