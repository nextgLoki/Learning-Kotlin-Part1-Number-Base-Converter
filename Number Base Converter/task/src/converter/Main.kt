package converter

const val COMMAND_EXIT = "/exit"
const val COMMAND_BACK = "/back"

fun readStringOrExit(): String {
    val s = readLine()!!
    return when {
        s == COMMAND_EXIT -> s
        s.split(" ").size == 2 -> s
        else -> readStringOrExit()
    }
}

fun main() {
    do {
        println("Enter two numbers in format: {source base} {target base} (To quit type /exit) ")
        val (sourceBase, targetBase) = when (val line = readStringOrExit()) {
            COMMAND_EXIT -> return
            else -> line.split(" ")
        }

        do {
            println("Enter number in base $sourceBase to convert to base $targetBase (To go back type /back)")
            val sourceNumber = when (val line = readLine()!!) {
                COMMAND_BACK -> break
                else -> line
            }
            println("Conversion result: ${NumberUtils.convert(sourceNumber, sourceBase, targetBase)}")
        } while (true)
    } while (true)
}