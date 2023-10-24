package org.jetbrains.kotlin.test.task.tamagotchi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TamagotchiApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<TamagotchiApplication>(*args)
}
