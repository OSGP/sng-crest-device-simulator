// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.crestdevicesimulator.simulator

import com.fasterxml.jackson.databind.ObjectMapper
import org.eclipse.californium.core.CoapClient
import io.github.oshai.kotlinlogging.KotlinLogging
import org.eclipse.californium.core.coap.MediaTypeRegistry
import org.eclipse.californium.core.coap.Request
import org.eclipse.californium.elements.exception.ConnectorException
import org.gxf.crestdevicesimulator.configuration.SimulatorProperties
import org.gxf.crestdevicesimulator.simulator.coap.CoapClientService
import org.gxf.crestdevicesimulator.simulator.response.ResponseHandler
import org.springframework.core.io.ClassPathResource
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.io.IOException

@Service
class Simulator(
        private val simulatorProperties: SimulatorProperties,
        private val coapClientService: CoapClientService,
        private val responseHandler: ResponseHandler) {

    private val logger = KotlinLogging.logger {}

    init {
        logger.info { "Simulator config started with config: $simulatorProperties" }
    }

    @Scheduled(fixedDelay = 5000, initialDelay = 0)
    fun sendPostMessage() {
        val jsonNode = ObjectMapper().readTree(ClassPathResource(simulatorProperties.messagePath).file)
        val payload = if (simulatorProperties.produceValidCbor) CborFactory.createValidCbor(jsonNode) else CborFactory.createInvalidCbor()
        val request =
                Request.newPost()
                        .apply {
                            options.setContentFormat(MediaTypeRegistry.APPLICATION_CBOR)
                        }.setPayload(payload)

        logger.info { "SEND REQUEST $request" }

        request(request)
    }

    private fun request(request: Request) {
        var coapClient: CoapClient? = null
        try {
            coapClient = coapClientService.createCoapClient()
            val response = coapClient.advanced(request)
            responseHandler.handleResponse(response)
            logger.info { "RESPONSE $response" }
        } catch (e: ConnectorException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (coapClient != null) coapClientService.shutdownCoapClient(coapClient)
        }
    }
}
