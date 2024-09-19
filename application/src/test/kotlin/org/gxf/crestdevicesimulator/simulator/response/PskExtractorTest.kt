// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0
package org.gxf.crestdevicesimulator.simulator.response

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class PskExtractorTest {
    @ParameterizedTest
    @CsvSource(
        "$VALID_PSK_COMMAND, true",
        "$VALID_PSK_COMMAND_WITH_KEY_WORDS_IN_KEY, true",
        "$INVALID_KEY_SIZE_PSK_COMMAND, false",
        "$NOT_PSK_COMMAND, false"
    )
    fun shouldReturnTrueWhenThereIsApskCommandInString(pskCommand: String, isValid: Boolean) {
        val result = PskExtractor.hasPskSetCommand(pskCommand)
        assertThat(result).isEqualTo(isValid)
    }

    @ParameterizedTest
    @CsvSource(
        "$VALID_PSK_COMMAND, 1234567891234567", "$VALID_PSK_COMMAND_WITH_KEY_WORDS_IN_KEY, PSKaSET1PSKd2SET")
    fun shouldReturnPskKeyFromValidPskCommand(pskCommand: String, expectedKey: String) {
        val result = PskExtractor.extractKeyFromCommand(pskCommand)

        assertThat(result).isEqualTo(expectedKey)
    }

    @ParameterizedTest
    @CsvSource("$VALID_PSK_COMMAND, $TEST_HASH", "$VALID_PSK_COMMAND_WITH_KEY_WORDS_IN_KEY, $TEST_HASH")
    fun shouldReturnHashFromValidPskCommand(pskCommand: String, expectedHash: String) {
        val result = PskExtractor.extractHashFromCommand(pskCommand)

        assertThat(result).isEqualTo(expectedHash)
    }

    @Suppress("WRONG_DECLARATIONS_ORDER")  // Prevent diktat from moving TEST_HASH
    companion object {
        private const val TEST_HASH = "1234567890123456123456789012345612345678901234561234567890123456"
        private const val INVALID_KEY_SIZE_PSK_COMMAND = "!PSK:1234:$TEST_HASH;PSK:1234:$TEST_HASH:SET"
        private const val NOT_PSK_COMMAND = "NoPskCommandInThisString"
        private const val VALID_PSK_COMMAND = "!PSK:1234567891234567:$TEST_HASH;PSK:1234567891234567:$TEST_HASH:SET"
        private const val VALID_PSK_COMMAND_WITH_KEY_WORDS_IN_KEY =
            "!PSK:PSKaSET1PSKd2SET:$TEST_HASH;PSK:PSKaSET1PSKd2SET:$TEST_HASH:SET"
    }
}
