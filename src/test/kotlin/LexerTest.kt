import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*;

// todo ;)
class LexerTest {

    @Test
    fun parenOpen() {
        val input = "("
        val scanner = Lexer(input)

        val expected = listOf(
                Token(TokenType.LEFT_PAREN, "(", null, 1),
                Token(TokenType.EOF, "", null, 1)
        )

        assertEquals(expected, scanner.scanTokens())
    }

    @Test
    fun parenClose() {
        val input = ")"
        val scanner = Lexer(input)

        val expected = listOf(
                Token(TokenType.RIGHT_PAREN, ")", null, 1),
                Token(TokenType.EOF, "", null, 1)
        )

        assertEquals(expected, scanner.scanTokens())
    }
}