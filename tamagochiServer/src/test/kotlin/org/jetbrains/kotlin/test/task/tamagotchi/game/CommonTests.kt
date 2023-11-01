package org.jetbrains.kotlin.test.task.tamagotchi.game

import org.jetbrains.academy.test.system.core.models.method.TestMethodInvokeData
import org.jetbrains.kotlin.test.task.tamagotchi.game.ClassStructureTests.Companion.EXPECTED_MAX_CAPACITY
import org.jetbrains.kotlin.test.task.tamagotchi.models.Command
import org.jetbrains.kotlin.test.task.tamagotchi.models.Mode
import org.jetbrains.kotlin.test.task.tamagotchi.testData.addCommandGameServiceMethod
import org.jetbrains.kotlin.test.task.tamagotchi.testData.gameServiceClass
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.lang.reflect.InvocationTargetException
import kotlin.random.Random

class CommonTests {
    @Test
    fun addCommandTestMethodTest() {
        val invokeData = TestMethodInvokeData(gameServiceClass, addCommandGameServiceMethod)
        assertThrows<InvocationTargetException>("\"The method ${addCommandGameServiceMethod.name} must throw an exception for index = -1.") {
            callAddCommandGameServiceMethod(invokeData, -1)
        }
        repeat(EXPECTED_MAX_CAPACITY) {
            val actual = callAddCommandGameServiceMethod(invokeData, Random.nextInt(4))
            assert(actual)
        }
    }

    @Test
    fun addCommandOverMaximumTest() {
        abstractGameServiceTest(
            List(2 * EXPECTED_MAX_CAPACITY) { Add(randomCommand()) },
            List(EXPECTED_MAX_CAPACITY) { true } + List(EXPECTED_MAX_CAPACITY) { false }
        )
    }

    @Test
    fun checkImmutabilityGetAllCommandsTest() {
        val service = GameService()
        repeat(5) {
            service.addCommand(randomCommand())
        }
        val commands = service.getAllCommands()
        commands.removeFirst()
        assertNotEquals(
            service.getAllCommands(),
            commands
        ) { "Field `commands` cannot be changed by user, only by method `addCommand` and `getCommand`" }
    }

    @RepeatedTest(20)
    fun getCommandTestMethodMixedRandomTest() {
        val commandsStore = ArrayDeque(List(EXPECTED_MAX_CAPACITY) { randomCommand() })
        val expectedGetAll = commandsStore.map { Command.values()[it] }
        val addActions = commandsStore.map { Add(it) }
        val getAction = List(EXPECTED_MAX_CAPACITY) { Get(Mode.values().random()) }
        val expected = getAction.map {
            when (it.mode) {
                Mode.Queue -> commandsStore.removeFirst()
                Mode.Stack -> commandsStore.removeLast()
            }.let { command -> Command.values()[command] }
        }
        abstractGameServiceTest(
            addActions + listOf(GetAll) + getAction,
            List(addActions.size) { true } + listOf(expectedGetAll) + expected
        )
    }

    @RepeatedTest(20)
    fun getAllCommandsRandomTest() {
        val addedCommands = List(EXPECTED_MAX_CAPACITY) { randomCommand() }
        val actions = List(EXPECTED_MAX_CAPACITY) { listOf(Add(addedCommands[it]), GetAll) }.flatten()
        val expected = List(EXPECTED_MAX_CAPACITY) {
            listOf(
                true,
                addedCommands.subList(0, it + 1).map { command -> Command.values()[command] })
        }.flatten()
        abstractGameServiceTest(
            actions + listOf(Add(addedCommands[randomCommand()]), GetAll),
            expected + listOf(false, addedCommands.map { Command.values()[it] })
        )
    }

    @ParameterizedTest
    @MethodSource("getCommandTestMethodTestQueueData", "getCommandTestMethodTestStackData")
    fun abstractGameServiceTest(actions: List<Action>, expected: List<Any?>) {
        val service = GameService()
        val actual = actions.map { it.execute(service) }
        assertEquals(expected, actual) {
            beautifulErrorMessage(actions, expected, actual)
        }
    }

    private fun beautifulErrorMessage(actions: List<Action>, expected: List<Any?>, actual: List<Any?>) =
        "List of actions and results:\n" + actions.indices.joinToString("\n") {
            "${actions[it]} " + (
                    if (expected[it] == actual[it]) {
                        "==> `${expected[it]}`, as expected"
                    } else {
                        "!!==> expected: `${expected[it]}`, but actual: `${actual[it]}`"
                    })
        } + "\n"

    sealed class Action {
        abstract fun execute(service: GameService): Any?
    }

    data class Add(val command: Int) : Action() {
        override fun execute(service: GameService) = service.addCommand(command)

        override fun toString(): String {
            return "Add(${Command.values()[command]})"
        }
    }

    data class Get(val mode: Mode) : Action() {
        override fun execute(service: GameService) = service.getCommand(mode)
    }

    data object GetAll : Action() {
        override fun execute(service: GameService) = service.getAllCommands()
    }

    companion object {
        private const val EAT = 0
        private const val SLEEP = 1
        private const val CLEAN = 2
        private const val PLAY = 3

        private fun callAddCommandGameServiceMethod(invokeData: TestMethodInvokeData, command: Int) =
            gameServiceClass.invokeMethodWithArgs(
                args = arrayOf(command),
                invokeData = invokeData,
                isPrivate = false
            ) as Boolean

        private fun randomCommand() = Random.nextInt(Command.values().size)

        @JvmStatic
        val commandTestMethodTestQueueData = listOf(
            Arguments.of(listOf(Get(Mode.Queue)), listOf(null)),
            Arguments.of(listOf(Add(EAT), Get(Mode.Queue)), listOf(true, Command.Eat)),
            Arguments.of(
                listOf(Add(EAT), Get(Mode.Queue)),
                listOf(true, Command.Eat)
            ),
            Arguments.of(
                listOf(Add(SLEEP), Get(Mode.Queue)),
                listOf(true, Command.Sleep)
            ),
            Arguments.of(
                listOf(Add(PLAY), Add(SLEEP), Get(Mode.Queue)),
                listOf(true, true, Command.Play)
            ),
            Arguments.of(
                listOf(Add(CLEAN), Add(SLEEP), Get(Mode.Queue), Get(Mode.Queue)),
                listOf(true, true, Command.Clean, Command.Sleep)
            ),
            Arguments.of(
                listOf(
                    Add(PLAY), Add(PLAY), Add(PLAY), Add(EAT), Add(PLAY),
                    Get(Mode.Queue), Get(Mode.Queue), Get(Mode.Queue), Get(Mode.Queue), Get(Mode.Queue)
                ),
                listOf(
                    true,
                    true,
                    true,
                    true,
                    true,
                    Command.Play,
                    Command.Play,
                    Command.Play,
                    Command.Eat,
                    Command.Play
                )
            ),
            Arguments.of(
                listOf(
                    Add(PLAY), Add(PLAY), Add(PLAY), Add(EAT), Add(PLAY),
                    Get(Mode.Queue), Get(Mode.Queue), Get(Mode.Queue), Get(Mode.Queue), Get(Mode.Queue), Get(Mode.Queue)
                ),
                listOf(
                    true,
                    true,
                    true,
                    true,
                    true,
                    Command.Play,
                    Command.Play,
                    Command.Play,
                    Command.Eat,
                    Command.Play,
                    null
                )
            ),
            Arguments.of(
                listOf(
                    Add(CLEAN), Add(SLEEP),
                    Get(Mode.Queue), Get(Mode.Queue), Get(Mode.Queue), Get(Mode.Queue), Get(Mode.Queue)
                ),
                listOf(true, true, Command.Clean, Command.Sleep, null, null, null)
            ),
        )

        @JvmStatic
        val commandTestMethodTestStackData = listOf(
            Arguments.of(listOf(Get(Mode.Stack)), listOf(null)),
            Arguments.of(listOf(Add(EAT), Get(Mode.Stack)), listOf(true, Command.Eat)),
            Arguments.of(
                listOf(Add(EAT), Get(Mode.Stack)),
                listOf(true, Command.Eat)
            ),
            Arguments.of(
                listOf(Add(SLEEP), Get(Mode.Stack)),
                listOf(true, Command.Sleep)
            ),
            Arguments.of(
                listOf(Add(PLAY), Add(SLEEP), Get(Mode.Stack)),
                listOf(true, true, Command.Sleep)
            ),
            Arguments.of(
                listOf(Add(CLEAN), Add(SLEEP), Get(Mode.Stack), Get(Mode.Stack)),
                listOf(true, true, Command.Sleep, Command.Clean)
            ),
            Arguments.of(
                listOf(
                    Add(PLAY), Add(PLAY), Add(PLAY), Add(EAT), Add(PLAY),
                    Get(Mode.Stack), Get(Mode.Stack), Get(Mode.Stack), Get(Mode.Stack), Get(Mode.Stack)
                ),
                listOf(
                    true,
                    true,
                    true,
                    true,
                    true,
                    Command.Play,
                    Command.Eat,
                    Command.Play,
                    Command.Play,
                    Command.Play
                )
            ),
            Arguments.of(
                listOf(
                    Add(PLAY), Add(PLAY), Add(PLAY), Add(EAT), Add(PLAY),
                    Get(Mode.Stack), Get(Mode.Stack), Get(Mode.Stack), Get(Mode.Stack), Get(Mode.Stack), Get(Mode.Stack)
                ),
                listOf(
                    true,
                    true,
                    true,
                    true,
                    true,
                    Command.Play,
                    Command.Eat,
                    Command.Play,
                    Command.Play,
                    Command.Play,
                    null
                )
            ),
            Arguments.of(
                listOf(
                    Add(CLEAN), Add(SLEEP),
                    Get(Mode.Stack), Get(Mode.Stack), Get(Mode.Stack), Get(Mode.Stack), Get(Mode.Stack)
                ),
                listOf(true, true, Command.Sleep, Command.Clean, null, null, null)
            ),
        )
    }
}
