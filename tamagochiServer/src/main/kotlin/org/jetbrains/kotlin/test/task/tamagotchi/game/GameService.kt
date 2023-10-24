package org.jetbrains.kotlin.test.task.tamagotchi.game

import org.jetbrains.kotlin.test.task.tamagotchi.models.Command
import org.jetbrains.kotlin.test.task.tamagotchi.models.Mode
import org.springframework.stereotype.Service

@Service
class GameService {
    companion object {
        private const val MAX_CAPACITY = 16
    }
}
