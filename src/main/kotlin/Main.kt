fun main(args: Array<String>) {

    when {
        args.size > 1 -> {
            println("Usage: klox [script]")
            System.exit(64)
        }
        args.size == 1 -> Lox.runFile(args[0])
        else -> Lox.runPrompt()
    }

    System.exit(0)
}