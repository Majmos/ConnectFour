import java.lang.NumberFormatException

var firstPlayer = "First"
var secondPlayer = "Second"
var rows = 6
var columns = 7
var discs = MutableList(columns) { MutableList(rows) { " " } }
var turn = 1
var numberOfGames = 1
var gameNumber = 0
var firstPlayerScore = 0
var secondPlayerScore = 0

fun main() {
    greet()
    takeDimensions()
    takeNumberOfGames()
    printGameInfo()
    do {
        gameNumber++
        setGame()
        playGame()
        turn *= -1
    } while (numberOfGames != gameNumber)
}

fun greet() {
    println("Connect Four")
    println("First player's name:")
    firstPlayer = readLine()!!
    println("Second player's name:")
    secondPlayer = readLine()!!
}

fun askForDimension() {
    println("Set the board dimensions (Rows x Columns)")
    println("Press Enter for default (6 x 7)")
}

fun takeDimensions() {
    var isValid = false
    askForDimension()
    var input = readLine()!!
    while (input.isNotBlank() && !isValid) {
        if (!input.contains("x", true)) {
            println("Invalid input")
            askForDimension()
            input = readLine()!!
            continue
        }
        if (!input.trim().contains("[abcdefghijklmnopqrstuvwyz]".toRegex())) {
            val temp = input.split("x", "X").map { it.trim() }
            if (temp[0].isNotEmpty() && temp[1].isNotEmpty()) {
                if (temp[0].toInt() in 5..9) {
                    rows = temp[0].toInt()
                } else {
                    println("Board rows should be from 5 to 9")
                    askForDimension()
                    input = readLine()!!
                    continue
                }
                if (temp[1].toInt() in 5..9) {
                    columns = temp[1].toInt()
                } else {
                    println("Board columns should be from 5 to 9")
                    askForDimension()
                    input = readLine()!!
                    rows = 6
                    continue
                }
                isValid = true
            } else {
                println("Invalid input")
                askForDimension()
                input = readLine()!!
            }
        } else {
            println("Invalid input")
            askForDimension()
            input = readLine()!!
        }
    }
}

fun takeNumberOfGames() {
    do {
        println("Do you want to play single or multiple games?")
        println("For a single game, input 1 or press Enter")
        println("Input a number of games:")
        val input = readLine()!!
        if (input.isBlank()) {
            break
        }
        try {
            input.toInt()
        } catch (e: NumberFormatException) {
            println("Invalid input")
            continue
        }
        if (input.toInt() <= 0) {
            println("Invalid input")
            continue
        }
        numberOfGames = input.toInt()
        break
    } while (true)
}

fun printGameInfo() {
    println("$firstPlayer VS $secondPlayer")
    println("$rows X $columns board")
    if (numberOfGames == 1) {
        println("Single game")
    } else {
        println("Total $numberOfGames games")
    }
}

fun setGame() {
    if (numberOfGames > 1) {
        println("Game #$gameNumber")
    }
    discs = MutableList(columns) { MutableList(rows) { " " } }
    printBoard(rows, columns)
}

fun printBoard(rows: Int, columns: Int) {
    for (i in 0 until rows + 2) {
        for (j in 0 until columns) {
            when (i) {
                0 -> {
                    print(" ${j+1}")
                }
                rows + 1 -> {
                    when (j) {
                        0 -> {
                            print("╚═╩")
                        }
                        columns - 1 -> {
                            print("═╝")
                        }
                        else -> {
                            print("═╩")
                        }
                    }
                }
                else -> {
                    when (j) {
                        0 -> {
                            print("║${discs[j][i - 1]}║")
                        }
                        else -> {
                            print("${discs[j][i - 1]}║")
                        }
                    }
                }
            }
        }
        println()
    }
}

fun promptPlayer() {
    if (turn > 0) {
        println("$firstPlayer's turn:")
    } else println("$secondPlayer's turn:")
}

fun playGame() {
    do {
        promptPlayer()
        val input = readLine()!!
        if (input == "end") {
            println("Game over!")
            break
        }
        if(input.trim().contains("[abcdefghijklmnopqrstuvwxyz]".toRegex())) {
            println("Incorrect column number")
            continue
        }
        if (input.toInt() !in 1..columns) {
            println("The column number is out of range (1 - $columns)")
            continue
        }
        if (discs[input.toInt() - 1].lastIndexOf(" ") == -1) {
            println("Column ${input.toInt()} is full")
            continue
        }
        val placeRow = discs[input.toInt() - 1].lastIndexOf(" ")
        if (turn > 0) {
            discs[input.toInt() - 1][placeRow] = "o"
        } else discs[input.toInt() - 1][placeRow] = "*"
        printBoard(rows, columns)
        if (checkVictoryCondition(input.toInt() - 1, placeRow)) {
            if (turn > 0) {
                println("Player $firstPlayer won")
                firstPlayerScore += 2
            } else {
                println("Player $secondPlayer won")
                secondPlayerScore += 2
            }
            if (numberOfGames > 1) {
                printScore()
            }
            if (gameNumber == numberOfGames) {
                println("Game over!")
            }
            break
        }
        if (discs.all { !it.contains(" ") }){
            println("It is a draw")
            firstPlayerScore++
            secondPlayerScore++
            if (numberOfGames > 1) {
                printScore()
            }
            if (gameNumber == numberOfGames) {
                println("Game over!")
            }
            break
        }
        turn *= -1
    } while (true)
}

fun checkVictoryCondition(lastColumn: Int, lastRow: Int): Boolean {
    val ch = discs[lastColumn][lastRow]
    var counter = 0
    for (i in 0 until rows) {
        if (discs[lastColumn][i] != ch) {
            counter = 0
            continue
        }
        counter++
        if (counter == 4) {
            return true
        }
    }
    counter = 0
    for (i in 0 until columns) {
        if (discs[i][lastRow] != ch) {
            counter = 0
            continue
        }
        counter++
        if (counter == 4) {
            return true
        }
    }
    var tempColumn = lastColumn
    var tempRow = lastRow
    while (tempColumn > 0 && tempRow > 0) {
        tempColumn--
        tempRow--
    }
    counter = 0
    while (tempColumn < columns && tempRow < rows) {
        if (discs[tempColumn][tempRow] != ch) {
            tempColumn++
            tempRow++
            counter = 0
            continue
        }
        tempColumn++
        tempRow++
        counter++
        if (counter == 4) {
            return true
        }
    }
    tempColumn = lastColumn
    tempRow = lastRow
    while (tempColumn > 0 && tempRow < rows - 1) {
        tempColumn--
        tempRow++
    }
    counter = 0
    while (tempColumn < columns && tempRow > 0) {
        if (discs[tempColumn][tempRow] != ch) {
            tempColumn++
            tempRow--
            counter = 0
            continue
        }
        tempColumn++
        tempRow--
        counter++
        if (counter == 4) {
            return true
        }
    }
    return false
}

fun printScore() {
    println("Score")
    println("$firstPlayer: $firstPlayerScore $secondPlayer: $secondPlayerScore")
}