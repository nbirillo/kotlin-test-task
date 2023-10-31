package org.jetbrains.kotlin.test.task.tamagotchi.models

enum class Command {
    Eat,
    Sleep,
    Clean,
    Play;

    companion object {
        fun valueOf(value: Int): Command = Command.values().getOrNull(value)
            ?: error("Invalid value of command")
    }
}
