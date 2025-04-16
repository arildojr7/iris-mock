package dev.arildo.irismock.callmodifier

import dev.arildo.irismock.callmodifier.BehaviourModifier

internal data class DelayModifier(
    override val chainHashCode: Int,
    val timeMillis: Long
) : BehaviourModifier(chainHashCode)
