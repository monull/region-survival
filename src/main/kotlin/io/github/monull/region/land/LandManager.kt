package io.github.monull.region.land

import io.github.monull.region.Lands
import io.github.monull.region.plugin.RegionSurvivalPlugin
import org.bukkit.Bukkit
import org.bukkit.Location
import java.io.File

class LandManager(val plugin: RegionSurvivalPlugin) {
    val lands = HashMap<Location, Land>()
    fun loadLand() {
        val folder = File(plugin.dataFolder, "lands").apply { mkdirs() }
        folder.listFiles()?.forEach {
            val land = Land().apply {
                load(it)
                setBox()
            }
            lands[land.loc] = land
        }
        for (x in -5..5) {
            for (z in -5..5) {
                val loc = Location(Bukkit.getWorlds().first(), x * 9.0, 0.0, z * 9.0)
                if (lands[loc] == null) {
                    lands[loc] = Land().apply {
                        this.loc = loc
                        locx = loc.x
                        locy = loc.y
                        locz = loc.z
                        name = "$locx$locz.yml"
                        file = File(folder, name)
                        setBox()
                    }
                }
            }
        }

        lands.values.forEach {
            Lands.lands += it
        }
    }

    fun saveLand() {
        Lands.lands.forEach {
            it.save()
        }
    }
}