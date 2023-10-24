# kotlin-test-task

The test task consists of two parts: a game implementation and tests for it.
All GitHub actions must be green.

## Tamagotchi game

This repository already contains part of the Tamagotchi game implementation. 
The client part is fully implemented, built and placed in the resources' folder. 
You don't need to modify it.

The main goal is to implement the `GameService` and `GameResource` classes. 
`GameResource` already has all the methods needed for the game, 
you just need to use the necessary methods from the `service`. 
`GameService` is now an almost empty class. 
You need to add all the necessary methods so that you can use them in `GameResource`.

### The game description

The first thing to do is to add a storage for the commands that will be applied to the Tamagotchi commands. 
This storage should support two modes: `queue` and `stack` (see the enum class `Mode`), 
which means that you need to choose the necessary structure to optimise the `pop` and `pull` operations.
**Note**: you don't have to implement this from scratch, please use built-in Java or Kotlin collection implementations.

Also, you need to support two operations: `addCommand` and `getCommand`:

- The `addCommand` function should add the `command` into the `commands`  storage in the end of the collection 
if `commands` has less than `MAX_CAPACITY` elements. The function returns if the `command` has been added.

- The `getCommand` function should return `null` if the `commands` storage is empty.
Otherwise, it should get a command from `commands` according to the `mode`(`queue` or `stack`).

If you implement everything successfully, after runnig the application the game will work as follows:
![Final application](./img/ready.gif).

To run the application, you need to run the `main` function inside
the `TamagotchiApplication.kt` file. ext, you need to open any browser (we recommend using [Google Chrome](https://www.google.com/chrome/) to display the elements as in the examples)
and open http://localhost:8080/. You will see the main page of the application.

## Tests

You need to implement ___ test functions. 
Some of them you must implement by using the [Kotlin test framework](https://github.com/jetbrains-academy/kotlin-test-framework) 
since it is actively used in the IDE Kotlin courses. Some of them should be implemented as common tests with JUnit5.

The tests list:

- `commandFieldTest` (with the test framework) - get the commands storage and check it's initial value, it should store an empty collection
- `gameServiceTestClassTest` (with the test framework) - a simple test that checks the base definition of the class and also checks the fields definition
- `addCommandTestMethodTest` (with the test framework) - checks that the `addCommand` function works correctly
- `getCommandTestMethodStackTest`, `getCommandTestMethodQueueTest` (regular JUnit5 tests) - check for both modes that the `getCommand` function works properly. 
You can combine these tests if you wish.
