package dev.arildo.iris.mock.callmodifier

internal data class DelayModifier(
    override val chainHashCode: Int,
    val timeMillis: Long
) : BehaviourModifier(chainHashCode)
