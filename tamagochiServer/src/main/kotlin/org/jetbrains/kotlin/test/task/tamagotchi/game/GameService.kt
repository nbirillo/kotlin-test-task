package org.jetbrains.kotlin.test.task.tamagotchi.game

import org.jetbrains.kotlin.test.task.tamagotchi.models.Command
import org.jetbrains.kotlin.test.task.tamagotchi.models.Mode
import org.springframework.stereotype.Service

@Service
class GameService {
    companion object {
        private const val MAX_CAPACITY = 16
    }

    private val commands = ArrayDeque<Command>()

    fun getCommand(mode: Mode) =
        if (commands.isNotEmpty()) {
            when (mode) {
                Mode.Queue -> commands.removeFirst()
                Mode.Stack -> commands.removeLast()
            }
        } else {
            null
        }

    fun addCommand(command: Int) =
        if (commands.size <= MAX_CAPACITY) {
            commands.add(Command.valueOf(command))
            true
        } else {
            false
        }

    fun getAllCommands() = ArrayDeque(commands)
}
