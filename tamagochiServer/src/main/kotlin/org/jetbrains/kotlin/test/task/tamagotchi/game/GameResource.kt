@file:Suppress("UnusedParameter")

package org.jetbrains.kotlin.test.task.tamagotchi.game

import org.jetbrains.kotlin.test.task.tamagotchi.models.Command
import org.jetbrains.kotlin.test.task.tamagotchi.models.Mode
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/command/")
class GameResource(val service: GameService) {
    @CrossOrigin
    @PostMapping("/get")
    fun getCommand(@RequestBody mode: String): Command? = TODO("Not implemented yet")

    @CrossOrigin
    @PostMapping("/add")
    fun addCommand(@RequestBody command: Int): Boolean = TODO("Not implemented yet")

    @CrossOrigin
    @GetMapping("/all")
    fun getAllCommands(): ArrayDeque<Command> = TODO("Not implemented yet")
}
