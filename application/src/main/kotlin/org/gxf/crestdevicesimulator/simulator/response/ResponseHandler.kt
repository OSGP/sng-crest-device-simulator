// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.crestdevicesimulator.simulator.response

import org.eclipse.californium.core.CoapResponse
import org.gxf.crestdevicesimulator.simulator.response.command.PskCommandHandler
import org.springframework.stereotype.Component

@Component
class ResponseHandler(private val pskCommandHandler: PskCommandHandler,
                      private val pskKeyExtractor: PskKeyExtractor) {

    fun handleResponse(response: CoapResponse) {
        val body = String(response.payload)

        if (pskKeyExtractor.hasPskCommand(body)) {
            pskCommandHandler.handlePskChange(body)
        }
    }
}
