// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0
package org.gxf.crestdevicesimulator.simulator.message

import org.gxf.crestdevicesimulator.configuration.SimulatorProperties
import org.gxf.crestdevicesimulator.simulator.CborFactory
import org.gxf.crestdevicesimulator.simulator.coap.CoapClientService
import org.gxf.crestdevicesimulator.simulator.response.CommandService
import org.gxf.crestdevicesimulator.simulator.response.command.PskService

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock
import org.springframework.util.ResourceUtils

class MessageHandlerTests {
    private val mapper = ObjectMapper()
    private val simulatorProperties: SimulatorProperties = mock()
    private val coapClientService: CoapClientService = mock()
    private val pskService: PskService = mock()
    private val commandService: CommandService = mock()
    private val messageHandler = MessageHandler(coapClientService, simulatorProperties, pskService, mapper,
        commandService)

    @Test
    fun shouldSendInvalidCborWhenTheMessageTypeIsInvalidCbor() {
        `when`(simulatorProperties.produceValidCbor).thenReturn(false)
        val message = mapper.readTree(testFile())

        val request = messageHandler.createRequest(message)

        assertThat(request.payloadString).isEqualTo(CborFactory.INVALID_CBOR_MESSAGE)
    }

    @Test
    fun shouldSendCborFromConfiguredJsonFileWhenTheMessageTypeIsCbor() {
        `when`(simulatorProperties.produceValidCbor).thenReturn(true)
        val message = mapper.readTree(testFile())

        val request = messageHandler.createRequest(message)
        val expected = CBORMapper().writeValueAsBytes(message)
        assertThat(request.payload).containsExactly(expected.toTypedArray())
    }

    private fun testFile() = ResourceUtils.getFile("classpath:test-file.json")
}
