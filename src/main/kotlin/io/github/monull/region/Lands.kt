package io.github.monull.region

import io.github.monull.region.land.Land
import org.bukkit.Location
import org.bukkit.entity.Player

object Lands {
    var lands = arrayListOf<Land>()
}

val Location.nearestLand: Land
    get() {
        var distance = 100.0
        var nearest: Land? = null
        Lands.lands.forEach {
            val dis = it.loc.distance(this)
            if (dis < distance) {
                distance = dis
                nearest = it
            }
        }
        return nearest!!
    }

fun Player.canAccessLand(land: Land): Boolean {
    if (name == land.owner || land.owner == "null") {
        return true
    } else {
        return false
    }
}