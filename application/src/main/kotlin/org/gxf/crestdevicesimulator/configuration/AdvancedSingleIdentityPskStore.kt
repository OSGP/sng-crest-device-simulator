// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0
package org.gxf.crestdevicesimulator.configuration

import org.eclipse.californium.scandium.dtls.ConnectionId
import org.eclipse.californium.scandium.dtls.HandshakeResultHandler
import org.eclipse.californium.scandium.dtls.PskPublicInformation
import org.eclipse.californium.scandium.dtls.PskSecretResult
import org.eclipse.californium.scandium.dtls.pskstore.AdvancedPskStore
import org.eclipse.californium.scandium.util.SecretUtil
import org.eclipse.californium.scandium.util.ServerNames
import org.springframework.beans.factory.annotation.Value

import java.net.InetSocketAddress
import javax.crypto.SecretKey

/**
 * @param identity
 */
class AdvancedSingleIdentityPskStore(private val identity: String) : AdvancedPskStore {
    var key: String = ""

    @Value("\${simulator.config.psk-key}")
    lateinit var defaultKey: String

    override fun hasEcdhePskSupported() = true

    override fun requestPskSecretResult(
        cid: ConnectionId?,
        serverName: ServerNames?,
        identity: PskPublicInformation,
        hmacAlgorithm: String?,
        otherSecret: SecretKey?,
        seed: ByteArray?,
        useExtendedMasterSecret: Boolean
    ): PskSecretResult {
        if (key.isEmpty()) {
            return PskSecretResult(cid, identity, SecretUtil.create(defaultKey.toByteArray(), ALGORITHM))
        }
        return PskSecretResult(cid, identity, SecretUtil.create(key.toByteArray(), ALGORITHM))
    }

    override fun getIdentity(peerAddress: InetSocketAddress?, virtualHost: ServerNames?) =
        PskPublicInformation(identity)

    override fun setResultHandler(resultHandler: HandshakeResultHandler?) {
        // No async handler is used, so no implementation needed
    }

    companion object {
        private const val ALGORITHM = "PSK"
    }
}
