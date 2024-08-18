package com.example.klox

import java.util.Scanner as JavaScanner

fun runScanner(input: String) {
    val scanner = Scanner(input)
    val tokens = scanner.scanTokens()

    for (token in tokens) {
        println(token)
    }
}

fun main() {

    println("Welcome to Klox Scanner REPL")
    println("Type 'exit' to quit")

    val inputScanner = JavaScanner(System.`in`)

    while (true) {
        print("> ")
        val input = inputScanner.nextLine()

        if (input.equals("exit", ignoreCase = true)) {
            break
        }

        runScanner(input)
    }
    inputScanner.close()

}