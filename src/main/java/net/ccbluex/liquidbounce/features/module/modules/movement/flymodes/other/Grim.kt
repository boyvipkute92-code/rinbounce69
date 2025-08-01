package net.ccbluex.liquidbounce.features.module.modules.movement.flymodes

import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.minecraft.network.play.client.C03PacketPlayer
import org.apache.commons.lang3.RandomUtils

object Grim : FlyMode("Grim") {
    private var initialY = 0.0
    private var ticks = 0

    override fun onEnable() {
        initialY = mc.thePlayer.posY
        ticks = 0
    }

    override fun onMotion(event: MotionEvent) {
        if (event.eventState != EventState.PRE) return
        
        ticks++
        
        val jitter = when (ticks % 4) {
            0 -> RandomUtils.nextDouble(1.0E-10, 1.0E-5)
            1 -> 0.0
            2 -> -RandomUtils.nextDouble(1.0E-10, 1.0E-5)
            else -> 0.0
        }
        
        mc.thePlayer.setPosition(
            mc.thePlayer.posX,
            initialY + jitter,
            mc.thePlayer.posZ
        )
        mc.thePlayer.motionY = 0.0
        mc.thePlayer.onGround = true
    }

    override fun onMove(event: MoveEvent) {
        event.y = 0.0
        mc.thePlayer.motionY = 0.0
        mc.thePlayer.posY = initialY
    }

    override fun onJump(event: JumpEvent) {
        event.cancelEvent()
    }
}