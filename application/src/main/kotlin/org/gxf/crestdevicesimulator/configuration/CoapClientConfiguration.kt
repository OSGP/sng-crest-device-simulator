// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.gxf.crestdevicesimulator.configuration

import org.gxf.crestdevicesimulator.simulator.data.entity.PreSharedKey
import org.gxf.crestdevicesimulator.simulator.data.repository.PskRepository
import org.springframework.context.annotation.Bean

@org.springframework.context.annotation.Configuration
class CoapClientConfiguration(private val simulatorProperties: SimulatorProperties,
                              private val pskRepository: PskRepository) {

    @Bean
    fun pskStore(): AdvancedSingleIdentityPskStore {
        val store = AdvancedSingleIdentityPskStore(simulatorProperties.pskIdentity)
        val savedKey = pskRepository.findById(simulatorProperties.pskIdentity)

        if (savedKey.isEmpty) {
            val initialPreSharedKey = PreSharedKey(simulatorProperties.pskIdentity, simulatorProperties.pskKey, simulatorProperties.pskSecret)
            pskRepository.save(initialPreSharedKey)
            store.key = simulatorProperties.pskKey
        } else {
            store.key = savedKey.get().preSharedKey
        }

        return store
    }
}
