package org.jetbrains.kotlin.test.task.tamagotchi.game

import org.jetbrains.academy.test.system.core.models.classes.ConstructorGetter
import org.jetbrains.academy.test.system.core.models.variable.TestVariable
import org.jetbrains.kotlin.test.task.tamagotchi.testData.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ClassStructureTests {
    @Test
    fun commandFieldTest() {
        val commandsVariable = getGameServiceField(commandsGameServiceVariable) as ArrayDeque<*>
        assertEquals(0, commandsVariable.size)
        { "The init value of the field ${commandsGameServiceVariable.name} must be empty." }
    }

    @Test
    fun maxCapacityFieldTest() {
        val maxCapacityVariable = getGameServiceField(maxCapacityGameServiceVariable) as Int
        assertEquals(EXPECTED_MAX_CAPACITY, maxCapacityVariable)
        { "The value of the field ${commandsGameServiceVariable.name} must be $EXPECTED_MAX_CAPACITY." }
    }

    @Test
    fun gameServiceTestClassTest() {
        val clazz = gameServiceClass.checkBaseDefinition()
        gameServiceClass.checkFieldsDefinition(clazz, false)
        gameServiceClass.checkConstructors(
            clazz, listOf(
                ConstructorGetter(),
            )
        )

        val companion = gameServiceCompanionClass.checkBaseDefinition()
        gameServiceCompanionClass.checkDeclaredMethods(companion)
    }

    companion object {
        const val EXPECTED_MAX_CAPACITY = 16

        private fun getGameServiceField(variable: TestVariable): Any? {
            val clazz = gameServiceClass.checkBaseDefinition()
            val instance = clazz.getConstructor().newInstance()
            gameServiceClass.checkFieldsDefinition(clazz)
            val field = clazz.declaredFields.find { it.name == variable.name }
                ?: error("In class `${gameServiceClass.getFullName()}` expected field `${variable.name}`")
            field.isAccessible = true
            return field.get(instance)
        }
    }
}
