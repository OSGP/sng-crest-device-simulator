// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0
package org.gxf.crestdevicesimulator.simulator.response.command

import org.gxf.crestdevicesimulator.configuration.AdvancedSingleIdentityPskStore
import org.gxf.crestdevicesimulator.configuration.SimulatorProperties
import org.gxf.crestdevicesimulator.simulator.data.entity.PreSharedKey
import org.gxf.crestdevicesimulator.simulator.data.entity.PreSharedKeyStatus
import org.gxf.crestdevicesimulator.simulator.data.repository.PskRepository
import org.gxf.crestdevicesimulator.simulator.response.command.exception.InvalidPskException
import org.gxf.crestdevicesimulator.simulator.response.command.exception.InvalidPskHashException

import org.apache.commons.codec.digest.DigestUtils
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class PskServiceTest {
    private val newKey = "7654321987654321"
    private val oldKey = "1234567891234567"
    private val secret = "secret"
    private val identity = "867787050253370"
    private val oldRevision = 0
    private val newRevision = 1
    private val pskRepository: PskRepository = mock()
    private val simulatorProperties: SimulatorProperties = mock()
    private val pskStore = AdvancedSingleIdentityPskStore(oldKey)
    private val pskService = PskService(pskRepository, simulatorProperties, pskStore)

    @BeforeEach
    fun setup() {
        whenever(simulatorProperties.pskIdentity).thenReturn(identity)
        val psk = PreSharedKey(identity, oldRevision, oldKey, secret, PreSharedKeyStatus.ACTIVE)
        whenever(simulatorProperties.pskIdentity).thenReturn(identity)
        whenever(pskRepository.findFirstByIdentityAndStatusOrderByRevisionDesc(identity, PreSharedKeyStatus.ACTIVE))
            .thenReturn(psk)
        pskStore.key = oldKey
    }

    @Test
    fun shouldSetNewPskInStoreWhenTheKeyIsValid() {
        val expectedHash = DigestUtils.sha256Hex("$secret$newKey")
        val pskCommand = "!PSK:$newKey:$expectedHash;PSK:$newKey:$expectedHash:SET"
        val psk = PreSharedKey(identity, newRevision, newKey, secret, PreSharedKeyStatus.PENDING)
        whenever(pskRepository.save(any<PreSharedKey>())).thenReturn(psk)

        val result = pskService.preparePendingKey(pskCommand)

        assertThat(result).isEqualTo(psk)
    }

    @Test
    fun shouldThrowErrorPskCommandIsInvalid() {
        val invalidHash = DigestUtils.sha256Hex("invalid")
        val pskCommand = "!PSK:$oldKey;PSK:$oldKey:$invalidHash:SET"

        val thrownException = catchException { pskService.preparePendingKey(pskCommand) }

        assertThat(thrownException).isInstanceOf(InvalidPskException::class.java)
        verify(pskRepository, never()).save(any())
        assertThat(pskStore.key).isEqualTo(oldKey)
    }

    @Test
    fun shouldThrowErrorWhenHashDoesNotMatch() {
        val invalidHash = DigestUtils.sha256Hex("invalid")
        val pskCommand = "!PSK:$oldKey:$invalidHash;PSK:$oldKey:$invalidHash:SET"

        val thrownException = catchException { pskService.preparePendingKey(pskCommand) }

        assertThat(thrownException).isInstanceOf(InvalidPskHashException::class.java)
        verify(pskRepository, never()).save(any())
        assertThat(pskStore.key).isEqualTo(oldKey)
    }
}
