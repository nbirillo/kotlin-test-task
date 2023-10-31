@file:Suppress("UnusedParameter")

package org.jetbrains.kotlin.test.task.tamagotchi.game

import org.jetbrains.kotlin.test.task.tamagotchi.models.Command
import org.jetbrains.kotlin.test.task.tamagotchi.models.Mode
import org.springframework.web.bind.annotation.*

private fun String.deleteFirstAndLastSymbols(): String =
    if (this.length < 2) {
        error("Length of string must be more than `1`, but actual `${this.length}`")
    } else {
        this.substring(1, this.length - 1)
    }

@RestController
@RequestMapping("/api/command/")
class GameResource(private val service: GameService) {
    @CrossOrigin
    @PostMapping("/get")
    fun getCommand(@RequestBody mode: String): Command? =
        service.getCommand(Mode.valueOf(mode.deleteFirstAndLastSymbols()))

    @CrossOrigin
    @PostMapping("/add")
    fun addCommand(@RequestBody command: Int) = service.addCommand(command)

    @CrossOrigin
    @GetMapping("/all")
    fun getAllCommands(): ArrayDeque<Command> = service.getAllCommands()
}
