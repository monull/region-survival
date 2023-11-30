package io.github.monull.region

import io.github.monull.region.land.Land
import io.github.monull.region.merchant.MerchantPlayer
import io.github.monull.region.plugin.RegionSurvivalPlugin
import io.github.monun.invfx.InvFX
import net.kyori.adventure.text.Component.text
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object Lands {
    var lands = arrayListOf<Land>()
    val nullLand = Land()
    var canAccessAll = true
    var merchantPlayers = arrayListOf<MerchantPlayer>()
    lateinit var plugin: RegionSurvivalPlugin

    fun findPlayer(player: Player): MerchantPlayer {
        return merchantPlayers.find { it.player == player } ?: MerchantPlayer(player).apply {
            merchantPlayers += this
        }
    }
}

val Location.nearestLand: Land
    get() {
        return Lands.lands.find { it.box.contain(x, z) } ?: Lands.nullLand
    }

fun Player.canAccessLand(land: Land): Boolean {
    if (name == land.owner || Lands.canAccessAll) {
        return true
    } else {
        return false
    }
}