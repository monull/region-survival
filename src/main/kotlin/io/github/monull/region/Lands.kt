package io.github.monull.region

import io.github.monull.region.land.Land
import org.bukkit.Location
import org.bukkit.entity.Player

object Lands {
    var lands = arrayListOf<Land>()
    val nullLand = Land()
}

val Location.nearestLand: Land
    get() {
        return Lands.lands.find { it.box.contain(x, z) } ?: Lands.nullLand
    }

fun Player.canAccessLand(land: Land): Boolean {
    if (name == land.owner || land.owner == "null") {
        return true
    } else {
        return false
    }
}