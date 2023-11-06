import org.jetbrains.academy.test.system.core.models.Visibility
import org.jetbrains.kotlin.test.task.tamagotchi.models.Command
import org.junit.jupiter.api.Test
import org.jetbrains.academy.test.system.core.models.method.TestMethodInvokeData
import org.jetbrains.academy.test.system.core.models.method.TestMethod
import org.jetbrains.kotlin.test.task.tamagotchi.game.GameService
import org.jetbrains.academy.test.system.core.models.TestKotlinType
import org.jetbrains.academy.test.system.core.models.classes.TestClass
import org.jetbrains.academy.test.system.core.models.variable.TestVariable
import org.jetbrains.academy.test.system.core.models.variable.VariableMutability

class GameServiceTests {

    @Test
    fun gameServiceTestClassTest() {
        val gameServiceClass = gameServiceTestClass.checkBaseDefinition()
        gameServiceTestClass.checkFieldsDefinition(gameServiceClass)
    }

    @Test
    fun commandFieldTest() {
        val gameServiceInstance = GameService()
        val gameServiceClass = gameServiceTestClass.checkBaseDefinition()

        val commandsField = gameServiceClass.declaredFields.find { it.name == "commands" }
            ?: throw IllegalArgumentException("The 'commands' field must be present in GameService.")

        commandsField.isAccessible = true

        if (Collection::class.java.isAssignableFrom(commandsField.type)) {
            val commands = commandsField.get(gameServiceInstance) as? Collection<*>
                ?: throw IllegalArgumentException("Field 'commands' value is not a Collection.")
            assert(commands.isEmpty()) { "The 'commands' collection should be empty." }
        } else {
            throw IllegalArgumentException("Field 'commands' is not a Collection.")
        }
    }


    @Test
    fun addCommandTestMethodTest() {
        val invokeData = addCommandMethod.getInvokeData()

        val isAdded = gameServiceTestClass.invokeMethodWithArgs(
            Command.Play,
            invokeData = invokeData,
            isPrivate = addCommandMethod.visibility == Visibility.PRIVATE
        ) as Boolean

        assert(isAdded)
    }

    private fun TestMethod.getInvokeData() = TestMethodInvokeData(
        gameServiceTestClass,
        this,
    )
}


val addCommandMethod = TestMethod(
    name = "addCommand",
    returnType = TestKotlinType("Boolean"),
    returnTypeJava = "Boolean",
    arguments = listOf(
        TestVariable(
            name = "command",
            javaType = "Command"
        )
    )
)

val getCommandMethod = TestMethod(
    name = "getCommand",
    returnType = TestKotlinType("Command?"),
    returnTypeJava = "Command?",
)

val getAllCommandsMethod = TestMethod(
    name = "getAllCommands",
    returnType = TestKotlinType("Deque<Command>"),
)

val setModeMethod = TestMethod(
    name = "setMode",
    returnType = TestKotlinType(""),
    returnTypeJava = "",
    arguments = listOf(
        TestVariable(
            name = "newMode",
            javaType = "Mode"
        )
    )

)

val myTypeAliasKotlinType = TestKotlinType(
    "Mode"
)

val gameServiceTestClass = TestClass(
    "GameService",
    "org.jetbrains.kotlin.test.task.tamagotchi.game",
    isDataClass = false,
    declaredFields = listOf(
        TestVariable(
            name = "commands",
            javaType = "deque",
            visibility = Visibility.PRIVATE,
            mutability = VariableMutability.VAR,
            isInPrimaryConstructor = false,
        ),

        TestVariable(
            name = "mode",
            kotlinType = myTypeAliasKotlinType,
            javaType = "Mode",
            value = "Mode.Queue",
            visibility = Visibility.PRIVATE,
            mutability = VariableMutability.VAR,
            isInPrimaryConstructor = false,
        ),

        TestVariable(
            name = "MAX_CAPACITY",
            javaType = "Int",
            visibility = Visibility.PRIVATE,
            mutability = VariableMutability.VAL,
            isInPrimaryConstructor = false,
        ),
    ),
    customMethods = listOf(
        addCommandMethod, getCommandMethod, getAllCommandsMethod, setModeMethod
    )
)
