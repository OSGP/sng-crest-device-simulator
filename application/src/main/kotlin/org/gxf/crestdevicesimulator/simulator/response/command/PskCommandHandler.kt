// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.crestdevicesimulator.simulator.response.command

import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.commons.codec.digest.DigestUtils
import org.gxf.crestdevicesimulator.configuration.AdvancedSingleIdentityPskStore
import org.gxf.crestdevicesimulator.configuration.SimulatorProperties
import org.gxf.crestdevicesimulator.simulator.data.repository.PskRepository
import org.gxf.crestdevicesimulator.simulator.response.PskExtractor
import org.gxf.crestdevicesimulator.simulator.response.command.exception.InvalidPskHashException
import org.springframework.stereotype.Service

@Service
class PskCommandHandler(private val pskRepository: PskRepository,
                        private val simulatorProperties: SimulatorProperties,
                        private val pskStore: AdvancedSingleIdentityPskStore) {

    private val logger = KotlinLogging.logger {}

    fun handlePskChange(body: String) {
        val newPsk = PskExtractor.extractKeyFromCommand(body)
        val hash = PskExtractor.extractHashFromCommand(body)

        val preSharedKeyOptional = pskRepository.findById(simulatorProperties.pskIdentity)

        if (preSharedKeyOptional.isEmpty) {
            logger.error { "No psk for identity: ${simulatorProperties.pskIdentity}" }
        }

        logger.info { "Validating hash for identity: ${simulatorProperties.pskIdentity}" }

        val preSharedKey = preSharedKeyOptional.get()
        val secret = preSharedKey.secret
        val expectedHash = DigestUtils.sha256Hex("$secret$newPsk")

        if (expectedHash != hash) {
            throw InvalidPskHashException("PSK set Hash for Identity ${simulatorProperties.pskIdentity} did not match")
        }

        pskRepository.save(preSharedKey.apply { this.preSharedKey = newPsk })
        pskStore.key = newPsk
    }
}
