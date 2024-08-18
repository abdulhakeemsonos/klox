package com.example.klox

import com.example.klox.TokenType.*

internal class Scanner(val source: String) {

    companion object {
        private val keywords: Map<String, TokenType> = mutableMapOf(
            "and" to AND,
            "class" to CLASS,
            "else" to ELSE,
            "false" to FALSE,
            "for" to FOR,
            "fun" to FUN,
            "if" to IF,
            "nil" to NIL,
            "or" to OR,
            "print" to PRINT,
            "return" to RETURN,
            "super" to SUPER,
            "this" to THIS,
            "true" to TRUE,
            "var" to VAR,
            "while" to WHILE
        )
    }

    private val tokens = mutableListOf<Token>()
    private var current = 0
    private var start = 0
    private var line = 1

    fun scanTokens(): List<Token>  {
        while (!isAtEnd()) {
            start = current
            scanToken()
        }
         tokens.add(Token(EOF, "", null, line))
        return tokens
    }

    private fun scanToken() {
        val c: Char = advance()
        when (c) {
            // Single-character tokens.
            '(' -> addToken(LEFT_PAREN)
            ')' -> addToken(RIGHT_PAREN)
            '{' -> addToken(LEFT_BRACE)
            '}' -> addToken(RIGHT_BRACE)
            ',' -> addToken(COMMA)
            '.' -> addToken(DOT)
            '-' -> addToken(MINUS)
            '+' -> addToken(PLUS)
            ';' -> addToken(SEMICOLON)
            '*' -> addToken(STAR)
            // Two character tokens
            '!' -> addToken(if (match('=')) BANG_EQUAL else BANG)
            '=' -> addToken(if (match('=')) EQUAL_EQUAL else EQUAL)
            '<' -> addToken(if (match('=')) LESS_EQUAL else LESS)
            '>' -> addToken(if (match('=')) GREATER_EQUAL else GREATER)
            // Considering slash and comment
            '/' -> {
                if (match('/')) {
                    // A comment goes until the end of the line.
                    while (peek() != '\n' && !isAtEnd()) advance()
                } else {
                    addToken(SLASH)
                }
            }
            ' ', '\r', '\t' -> Unit // Ignore whitespace.
            '\n' -> line ++ // Increment line
            '"' -> string()
            else -> {
                when {
                    isDigit(c) -> number()
                    isAlpha(c) -> identifier()
                    else -> throw RuntimeException("Unexpected character.")
                }
            }
        }
    }

    private fun identifier() {
        while (isAlphaNumeric(peek())) {
            advance()
        }

        val token = source.substring(start, current)
        val tokenType = keywords[token] ?: IDENTIFIER
        addToken(tokenType)
    }

    private fun number() {
        while (isDigit(peek())) {
            advance()
        }
        // Look for fractional part.
        if (peek() == '.' && isDigit(peekNext())) {
            advance()
            while (isDigit(peek())) advance()
        }

        addToken(NUMBER, source.substring(start, current).toDouble())
    }

    private fun string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line ++
            advance()
        }

        if (isAtEnd()) {
            throw RuntimeException("Unterminated string.")
        }

        // To go past closing part of the string (")
        advance()
        // Trim the surrounding quotes.
        val tokenText = source.substring(start + 1, current - 1)
        addToken(STRING, tokenText)
    }

    private fun match(expected: Char): Boolean {
        if (isAtEnd()) return false
        if (source[current] != expected) return false

        current++
        return true
    }

    private fun peek(): Char {
        return if (isAtEnd()) '\u0000' else source[current]
    }

    private fun peekNext(): Char {
        if (current + 1 >= source.length) return '\u0000'
        return source[current + 1]
    }

    private fun isAlpha(c: Char): Boolean {
        return (c in 'a'..'z') ||
                (c in 'A'..'Z') ||
                c == '_'
    }

    private fun isDigit(c: Char): Boolean {
        return c in '0'..'9'
    }

    private fun isAlphaNumeric(c: Char): Boolean {
        return isAlpha(c) || isDigit(c)
    }

    private fun isAtEnd(): Boolean {
        return current >= source.length
    }

    private fun advance(): Char {
        return source[current++]
    }

    private fun addToken(type: TokenType) {
        addToken(type, null)
    }

    private fun addToken(type: TokenType, literal: Any?) {
        val text = source.substring(start, current)
        tokens.add(Token(type, text, literal, line))
    }

}