// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0
package org.gxf.crestdevicesimulator.simulator.data.entity

import org.gxf.crestdevicesimulator.simulator.data.repository.PreSharedKeyCompositeKey

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.IdClass

/**
 * @property identity
 * @property revision
 * @property preSharedKey
 * @property secret
 * @property status
 */
@Entity
@IdClass(PreSharedKeyCompositeKey::class)
class PreSharedKey(
    @Id val identity: String,
    @Id val revision: Int,
    var preSharedKey: String,
    val secret: String,
    @Enumerated(EnumType.STRING) var status: PreSharedKeyStatus
)
