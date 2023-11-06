package org.jetbrains.kotlin.test.task.tamagotchi.game

import org.jetbrains.kotlin.test.task.tamagotchi.models.Command
import org.jetbrains.kotlin.test.task.tamagotchi.models.Mode
import org.springframework.stereotype.Service
import java.util.*


@Service
class GameService {

    private var commands: Deque<Command> = ArrayDeque()
    private var mode: Mode = Mode.Queue


    companion object {
        private const val MAX_CAPACITY = 16
    }

    fun addCommand(command: Command): Boolean {
        if (commands.size >= MAX_CAPACITY) {
            return false
        }

        when (mode) {
            Mode.Queue -> commands.offer(command)
            Mode.Stack -> commands.addLast(command)
        }
        return true
    }

    fun getCommand(): Command? = when (mode) {
        Mode.Queue -> commands.poll()
        Mode.Stack -> commands.pollLast()
    }

    fun getAllCommands(): ArrayDeque<Command> = ArrayDeque(commands)

    fun setMode(newMode: Mode) {
        mode = newMode
    }
}
