@file:Suppress("UnusedParameter")

package org.jetbrains.kotlin.test.task.tamagotchi.game

import org.jetbrains.kotlin.test.task.tamagotchi.models.Command
import org.jetbrains.kotlin.test.task.tamagotchi.models.Mode
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

fun String.sanitizeMode(): String = this.drop(1).dropLast(1)

@RestController
@RequestMapping("/api/command/")
class GameResource(private val service: GameService) {

    @CrossOrigin
    @PostMapping("/get")
    fun getCommand(@RequestBody mode: String): Command {
        val sanitizedMode = mode.sanitizeMode()
        try {
            service.setMode(Mode.valueOf(sanitizedMode))
            return service.getCommand() ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "No command available.")
        } catch (e: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid mode provided.")
        }
    }

    @CrossOrigin
    @PostMapping("/add")
    fun addCommand(@RequestBody command: Int): Boolean {
        if (command < 0 || command >= Command.entries.size) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid command index.")
        }
        return service.addCommand(Command.entries[command])
    }

    @CrossOrigin
    @GetMapping("/all")
    fun getAllCommands(): ArrayDeque<Command> {
        return service.getAllCommands()
    }
}
