package io.github.monull.region

import io.github.monull.region.land.Land
import io.github.monull.region.merchant.Merchant
import io.github.monull.region.merchant.MerchantPlayer
import io.github.monull.region.plugin.RegionSurvivalPlugin
import io.github.monun.invfx.InvFX
import net.kyori.adventure.text.Component.text
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object Lands {
    var merchants = arrayListOf<Merchant>()
    var lands = arrayListOf<Land>()
    val nullLand = Land().apply {
        loc = Location(Bukkit.getWorlds().first(), 99.0, 0.0, 99.0)
        locx = 99.0
        locz = 99.0
        locy = 0.0
    }
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
    if (name == land.owner || Lands.canAccessAll || land.whitelist.contains(name)) {
        return true
    } else {
        return false
    }
}