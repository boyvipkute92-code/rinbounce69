/*
 * RinBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/rattermc/rinbounce69
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.flymodes.ncp

import net.ccbluex.liquidbounce.features.module.modules.movement.Fly.startY
import net.ccbluex.liquidbounce.features.module.modules.movement.flymodes.FlyMode
import net.ccbluex.liquidbounce.utils.client.PacketUtils.sendPackets
import net.ccbluex.liquidbounce.utils.extensions.component1
import net.ccbluex.liquidbounce.utils.extensions.component2
import net.ccbluex.liquidbounce.utils.extensions.component3
import net.ccbluex.liquidbounce.utils.extensions.tryJump
import net.ccbluex.liquidbounce.utils.movement.MovementUtils.strafe
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition

object OldNCP : FlyMode("OldNCP") {
    override fun onEnable() {
        if (!mc.thePlayer.onGround) return

        val (x, y, z) = mc.thePlayer

        repeat(4) {
            sendPackets(
                C04PacketPlayerPosition(x, y + 1.01, z, false),
                C04PacketPlayerPosition(x, y, z, false)
            )
        }

        mc.thePlayer.tryJump()
        mc.thePlayer.swingItem()
    }

    override fun onUpdate() {
        if (startY > mc.thePlayer.posY)
            mc.thePlayer.motionY = -0.000000000000000000000000000000001

        if (mc.gameSettings.keyBindSneak.isKeyDown)
            mc.thePlayer.motionY = -0.2

        if (mc.gameSettings.keyBindJump.isKeyDown && mc.thePlayer.posY < startY - 0.1)
            mc.thePlayer.motionY = 0.2

        strafe()
    }
}
