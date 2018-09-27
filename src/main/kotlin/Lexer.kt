// In the book this class is called Scanner.
// Since Java already has a Scanner class, to avoid confusion, I called it Lexer instead.
class Lexer(sourceCode: String) {
    private val source: String = sourceCode
    private val tokens: MutableList<Token> = mutableListOf()

    private var start: Int = 0
    private var current: Int = 0
    private var line: Int = 1

    fun scanTokens(): List<Token> {
        while (!isAtEnd()) {

            // next lexeme
            start = current
            scanToken()
        }

        tokens.add(Token(TokenType.EOF, "", null, line))

        return tokens
    }

    private fun scanToken() {
        val nextToken = advance()

        when (nextToken) {
            '(' -> addToken(TokenType.LEFT_PAREN)
            ')' -> addToken(TokenType.RIGHT_PAREN)
            '{' -> addToken(TokenType.LEFT_BRACE)
            '}' -> addToken(TokenType.RIGHT_BRACE)
            ',' -> addToken(TokenType.COMMA)
            '.' -> addToken(TokenType.DOT)
            '-' -> addToken(TokenType.MINUS)
            '+' -> addToken(TokenType.PLUS)
            '*' -> addToken(TokenType.STAR)
            ';' -> addToken(TokenType.SEMICOLON)

            // maybe one character, maybe two characters
            '!' -> addToken(ifMatchNext('=', TokenType.BANG_EQUAL, TokenType.BANG))
            '=' -> addToken(ifMatchNext('=', TokenType.EQUAL_EQUAL, TokenType.EQUAL))
            '<' -> addToken(ifMatchNext('=', TokenType.LESS_EQUAL, TokenType.LESS))
            '>' -> addToken(ifMatchNext('=', TokenType.GREATER_EQUAL, TokenType.GREATER))

            '/' -> matchSlash()

            ' ' -> Unit
            '\t' -> Unit
            '\r' -> Unit
            '\n' -> line++

            '"' -> matchString()

             else -> {
                 when {
                     nextToken.isDigit() -> matchNumber()
                     nextToken.isLetterOrUnderscore() -> matchIdentifier()
                     else -> Lox.error(line, "Lexer found an unexpected character.")
                 }
             }
        }
    }

    private fun matchIdentifier() {
        while (peek().isAlphaNumeric()) advance()

        val text = source.substring(start, current)
        val type = keywords.getOrDefault(text, TokenType.IDENTIFIER)

        addToken(type)
    }

    private fun matchNumber() {
        while (peek().isDigit()) advance()

        if (peek() == '.' && peekNext().isDigit()) {
            advance()
            while (peek().isDigit()) advance()
        }

        addToken(TokenType.NUMBER, source.substring(start, current).toDouble())
    }

    private fun matchString() {
        while(peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++
            advance()
        }

        if(isAtEnd()) {
            Lox.error(line, "Lexer found an unterminated string.")
            return
        }

        // consume past closing "
        advance()

        val value: String = source.substring(start + 1, current - 1)

        addToken(TokenType.STRING, value)
    }


    private fun matchSlash() {
        val nextLineChar = '\n'

        // comment
        if (match('/')) {
            while(peek() != nextLineChar && !isAtEnd()) advance()
        }
        // division
        else {
            addToken(TokenType.SLASH)
        }
    }

    private fun ifMatchNext(next: Char, onTrue: TokenType, onFalse: TokenType): TokenType {
        return if (match(next)) onTrue else onFalse
    }

    private fun match(expected: Char): Boolean {
        if (isAtEnd()) return false
        if (source[current] != expected) return false

        current++
        return true
    }

    private fun peek(): Char {
        if (isAtEnd()) return '\u0000' // also known as '\0', but that is not recognized as a Char in Kotlin
        return source[current]
    }

    private fun peekNext(): Char {
        if (current + 1 >= source.length) return '\u0000'
        return source[current + 1]
    }

    private fun advance(): Char {
        current++
        return source[current - 1]
    }

    private fun addToken(type: TokenType) {
        addToken(type, null)
    }

    private fun addToken(type: TokenType, literal: Any?) {
        val text = source.substring(start, current)
        tokens.add(Token(type, text, literal, line))
    }

    // AlphaNumeric + Underscore (otherwise we would just use Char.isLetterOrDigit())
    private fun Char.isAlphaNumeric(): Boolean {
        return this.isLetterOrDigit() || this == '_'
    }

    private fun Char.isLetterOrUnderscore(): Boolean {
        return this.isLetter() || this == '_'
    }

    private fun isAtEnd(): Boolean {
        return current >= source.length
    }

    companion object {
        val keywords: Map<String, TokenType> = hashMapOf(
                "and" to TokenType.AND,
                "and" to TokenType.AND,
                "class" to TokenType.CLASS,
                "else" to TokenType.ELSE,
                "false" to TokenType.FALSE,
                "for" to TokenType.FOR,
                "fun" to TokenType.FUN,
                "if" to TokenType.IF,
                "nil" to TokenType.NIL,
                "or" to TokenType.OR,
                "print" to TokenType.PRINT,
                "return" to TokenType.RETURN,
                "super" to TokenType.SUPER,
                "this" to TokenType.THIS,
                "true" to TokenType.TRUE,
                "var" to TokenType.VAR,
                "while" to TokenType.WHILE)
    }
}
