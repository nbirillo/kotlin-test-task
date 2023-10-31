package org.jetbrains.kotlin.test.task.tamagotchi.testData

import org.jetbrains.academy.test.system.core.models.TestKotlinType
import org.jetbrains.academy.test.system.core.models.Visibility
import org.jetbrains.academy.test.system.core.models.classes.ClassType
import org.jetbrains.academy.test.system.core.models.classes.TestClass
import org.jetbrains.academy.test.system.core.models.method.TestMethod
import org.jetbrains.academy.test.system.core.models.variable.TestVariable
import org.jetbrains.academy.test.system.core.models.variable.VariableMutability


val commandsGameServiceVariable = TestVariable(
    name = "commands",
    javaType = "ArrayDeque",
    kotlinType = TestKotlinType("ArrayDeque"),
    visibility = Visibility.PRIVATE,
    mutability = VariableMutability.VAL,
    isStatic = false,
)

val addCommandGameServiceMethod = TestMethod(
    name = "addCommand",
    returnType = TestKotlinType("Boolean"),
    returnTypeJava = "Boolean",
    arguments = listOf(TestVariable("command", "Int", kotlinType = TestKotlinType("Int"))),
    visibility = Visibility.PUBLIC
)

val maxCapacityGameServiceVariable = TestVariable(
    name = "MAX_CAPACITY",
    javaType = "Int",
    kotlinType = TestKotlinType(
        "Int"
    ),
    visibility = Visibility.PRIVATE,
    mutability = VariableMutability.VAL,
    isStatic = true,
)
val gameServiceClass = TestClass(
    "GameService",
    "org.jetbrains.kotlin.test.task.tamagotchi.game",
    Visibility.PUBLIC,
    classType = ClassType.CLASS,
    declaredFields = listOf(
        maxCapacityGameServiceVariable,
        commandsGameServiceVariable,
    ),
    customMethods = listOf(
        TestMethod(
            name = "getCommand",
            returnType = TestKotlinType("Command", isNullable = true),
            returnTypeJava = "Command",
            arguments = listOf(TestVariable("mode", "Mode", kotlinType = TestKotlinType("Mode"))),
            visibility = Visibility.PUBLIC
        ),
        addCommandGameServiceMethod,
        TestMethod(
            name = "getAllCommands",
            returnType = TestKotlinType("ArrayDeque"),
            returnTypeJava = "ArrayDeque",
            visibility = Visibility.PUBLIC
        ),
    ),
    isDataClass = false,
)

val gameServiceCompanionClass = TestClass(
    "GameService\$Companion",
    "org.jetbrains.kotlin.test.task.tamagotchi.game"
)

