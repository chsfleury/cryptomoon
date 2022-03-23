package fr.chsfleury.cryptomoon.utils

import fr.chsfleury.cryptomoon.utils.BigDecimals.pretty
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigDecimal
import java.util.stream.Stream


internal class BigDecimalsTest {

    @ParameterizedTest
    @MethodSource("prettyTestCases")
    fun testPretty(input: String, output: String) {
        assertThat(BigDecimal(input).pretty()).isEqualTo(output)
    }

    companion object {
        @JvmStatic
        fun prettyTestCases(): Stream<Arguments> = Stream.of(
            Arguments.of("0.000087580", "<0.0001"),
            Arguments.of("0.000640460", "0.0006"),
            Arguments.of("0.000660460", "0.0007"),
            Arguments.of("0.004509880", "0.0045"),
            Arguments.of("0.038009780", "0.038"),
            Arguments.of("0.290093300", "0.2901"),
            Arguments.of("9.097837040", "9.0978"),
            Arguments.of("55.07669772", "55.077"),
            Arguments.of("258.92906595", "258.93"),
            Arguments.of("4505.65365271", "4,505.7"),
            Arguments.of("68721.93482129", "68,722"),
            Arguments.of("145721.93482129", "145.7K"),
            Arguments.of("1073270.1100", "1,073K"),
            Arguments.of("78565327.1100", "78.57M"),
            Arguments.of("785653271.100", "785.7M"),
            Arguments.of("7856532711.00", "7,857M"),
            Arguments.of("78565327110.03", "78.57B"),
            Arguments.of("785653271100.023", "785.7B"),
            Arguments.of("7856532711000.26", "7,857B"),
            Arguments.of("78565327110000.12", "78,565B"),
        )
    }
}