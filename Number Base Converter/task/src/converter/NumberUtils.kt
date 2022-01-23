package converter

import java.math.BigDecimal
import java.math.BigInteger
import kotlin.math.pow

const val MAX_FLOATING_DIGITS_COUNT = 5

object NumberUtils {
    fun convert(
        sourceNumber: String, sourceBase: String, targetBase: String,
        decimalSeparator: String = "."
    ): String {
        if (sourceBase == targetBase) {
            return sourceNumber
        }

        val numberParts = sourceNumber.split(decimalSeparator)
        val integer = convertInteger(numberParts[0], sourceBase, targetBase).let {
            if (it == "") "0"
            else it
        }

        return if (numberParts.size == 1) {
            integer
        } else {
            val floating = convertFloating(numberParts[1], sourceBase, targetBase)
            "${integer}.${floating}"
        }
    }

    private fun getLabel(n: Int): String = when {
        n <= 9 -> n.toString()
        else -> ('a' + n - 10).toString()
    }

    private fun getNumFromLabel(n: Char): Int = when (n) {
        in '0'..'9' -> n.digitToInt()
        else -> n - 'a' + 10
    }

    private fun convertInteger(integerSource: String, sourceBase: String, targetBase: String): String {
        // or just "".toBigInteger(radix) instead of realizing convert
        val targetBaseBig = targetBase.toBigInteger()
        val sourceBaseBig = sourceBase.toBigInteger()

        val numberBigDec = if (sourceBase != "10") {
            convertToDecimal(integerSource, sourceBaseBig)
        } else {
            integerSource.toBigInteger()
        }

        var reminder = numberBigDec % targetBaseBig
        var quotient = numberBigDec
        val convertedNumber = StringBuilder()
        while (quotient != BigInteger.ZERO) {
            convertedNumber.append(getLabel(reminder.toInt()))
            quotient /= targetBaseBig
            reminder = quotient % targetBaseBig
        }

        return convertedNumber.reversed().toString()
    }

    private fun convertToDecimal(n: String, b: BigInteger): BigInteger {
        val lowerNumber = n.lowercase()
        var decimal = BigInteger.ZERO
        var i = n.length - 1
        for (num in lowerNumber) {
            val basePow = b.toDouble().pow(i.toDouble()).toBigDecimal().toBigInteger()
            decimal += getNumFromLabel(num).toBigInteger() * basePow
            i--
        }
        return decimal
    }

    private fun convertFloating(floatingPart: String, sourceBase: String, targetBase: String): String {
        val sourceBaseBig = sourceBase.toInt()
        val numberBigDec = if (sourceBase != "10") {
            convertFloatingToDecimal(floatingPart, sourceBaseBig)
        } else {
            "0.${floatingPart}".toBigDecimal()
        }

        var integer = numberBigDec.toBigInteger()
        var fractional = numberBigDec - integer.toBigDecimal()
        val targetBaseBig = targetBase.toBigDecimal()
        val convertedNumber = StringBuilder()
        val maxLength = MAX_FLOATING_DIGITS_COUNT
        while (fractional != BigInteger.ZERO && convertedNumber.length < maxLength) {
            fractional *= targetBaseBig
            integer = fractional.toBigInteger()
            if (integer > BigInteger.ZERO) {
                fractional -= integer.toBigDecimal()
            }
            convertedNumber.append(getLabel(integer.toInt()))
        }

        return convertedNumber.toString()
    }

    private fun convertFloatingToDecimal(floatingPart: String, b: Int): BigDecimal {
        if (b == 10) {
            return "0.${floatingPart}".toBigDecimal()
        }
        val lowerNumber = floatingPart.lowercase()
        var convertedFloating = BigDecimal.ZERO
        for (i in floatingPart.indices) {
            val decNum = getNumFromLabel(lowerNumber[i]).toBigDecimal()
            val decPow = b.toDouble().pow(i + 1).toBigDecimal()
            convertedFloating += decNum.setScale(MAX_FLOATING_DIGITS_COUNT * 2) / decPow
        }
        return convertedFloating
    }
}