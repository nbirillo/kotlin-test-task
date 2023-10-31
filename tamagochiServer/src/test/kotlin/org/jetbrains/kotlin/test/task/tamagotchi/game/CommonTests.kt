package org.jetbrains.kotlin.test.task.tamagotchi.game

import org.jetbrains.academy.test.system.core.models.method.TestMethodInvokeData
import org.jetbrains.kotlin.test.task.tamagotchi.game.ClassStructureTests.Companion.EXPECTED_MAX_CAPACITY
import org.jetbrains.kotlin.test.task.tamagotchi.models.Command
import org.jetbrains.kotlin.test.task.tamagotchi.models.Mode
import org.jetbrains.kotlin.test.task.tamagotchi.testData.addCommandGameServiceMethod
import org.jetbrains.kotlin.test.task.tamagotchi.testData.gameServiceClass
import org.junit.jupiter.api.Assertions.assertEquals
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
        val service = GameService()
        val expected = ArrayDeque<Command>()
        repeat(EXPECTED_MAX_CAPACITY) {
            service.addCommand(randomCommand().also { expected.add(Command.valueOf(it)) })
        }
    }

    @ParameterizedTest
    @MethodSource("validValueOfTestData")
    fun validValueOfTest(command: Int, expected: Command) {
        val actual = Command.valueOf(command)
        assertEquals(expected, actual) { "Command.valueOf($command): expected: `$expected`, actual: $actual" }
    }

    @ParameterizedTest
    @MethodSource("invalidValueOfTestData")
    fun invalidValueOfTest(command: Int) {
        assertThrows<IllegalStateException>("\"The method `Command.valueOf` must throw an exception for command: `$command`") {
            Command.valueOf(command)
        }
    }

    @ParameterizedTest
    @MethodSource("getCommandTestMethodTestQueueData", "getCommandTestMethodTestStackData")
    fun abstractGameServiceTest(actions: List<Action>, expected: List<Any?>) {
        val service = GameService()
        val actual = ArrayList<Any?>()
        for (action in actions) {
            actual.add(
                when (action) {
                    is Add -> service.addCommand(action.command)
                    is Get -> service.getCommand(action.mode)
                    is GetAll -> service.getAllCommands()
                }
            )
        }
        assertEquals(expected, actual) {
            beautifulErrorMessage(actions, expected, actual)
        }
    }

    private fun beautifulErrorMessage(actions: List<Action>, expected: List<Any?>, actual: ArrayList<Any?>) =
        "List of actions and results:\n" + actions.indices.joinToString("\n") {
            "${actions[it]} " + (
                    if (expected[it] == actual[it]) {
                        "==> `${expected[it]}`"
                    } else {
                        "!!==> expected: `${expected[it]}`, but actual: `${actual[it]}`"
                    })
        } + "\n"

    sealed class Action
    data class Add(val command: Int) : Action() {
        override fun toString(): String {
            return "Add(${Command.valueOf(command)})"
        }
    }

    data class Get(val mode: Mode) : Action()
    data object GetAll : Action()

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
        fun validValueOfTestData(): List<Arguments> =
            List(Command.values().size) { index -> Arguments.of(index, Command.values()[index]) }

        @JvmStatic
        fun invalidValueOfTestData(): List<Arguments> =
            listOf(-1, -10, Command.values().size + 1, Command.values().size + 1000)
                .map { Arguments.of(it) }


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
