import org.jetbrains.kotlin.test.task.tamagotchi.game.GameService
import org.jetbrains.kotlin.test.task.tamagotchi.models.Command
import org.jetbrains.kotlin.test.task.tamagotchi.models.Mode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GameServiceTestJUnit {

    private fun setupGameServiceWithCommands(mode: Mode): GameService {
        val gameService = GameService()
        gameService.setMode(mode)
        gameService.addCommand(Command.Eat)
        gameService.addCommand(Command.Sleep)
        return gameService
    }

    @Test
    fun getCommandTestMethodStackTest() {
        val gameService = setupGameServiceWithCommands(Mode.Stack)
        val retrievedCommand = gameService.getCommand()
        assertEquals(Command.Sleep, retrievedCommand, "Should retrieve the last added command in Stack mode")
    }

    @Test
    fun getCommandTestMethodQueueTest() {
        val gameService = setupGameServiceWithCommands(Mode.Queue)
        val retrievedCommand = gameService.getCommand()
        assertEquals(Command.Eat, retrievedCommand, "Should retrieve the first added command in Queue mode")
    }
}
