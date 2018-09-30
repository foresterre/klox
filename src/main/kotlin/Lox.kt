import java.io.File

class Lox {

    companion object {
        private var hadError: Boolean = false

        fun runPrompt() {

            while (true) {
                print("> ")
                run(readLine().toString())
            }

        }

        fun runFile(path: String) {
            val bufferedReader = File(path).bufferedReader()
            val source = bufferedReader.use { it.readText() }

            run(source)
        }

        private fun run(source: String) {
            val scanner = Lexer(source)
            val tokens = scanner.scanTokens()

            tokens.forEach { println("token($it), ") }

            if (hadError) System.exit(65)
        }

        fun error(line: Int, message: String) {
            report(line, "", message)
        }

        private fun report(line: Int, where: String, message: String) {
            System.err.println("[line $line] Error $where: $message")
            hadError = true
        }
    }
}