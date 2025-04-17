package dev.arildo.irismock.callmodifier

internal data class DelayModifier(
    override val chainHashCode: Int,
    val timeMillis: Long
) : BehaviourModifier(chainHashCode)
