package io.github.monull.region.plugin

import io.github.monull.region.RegionKommand
import io.github.monull.region.land.LandManager
import io.github.monun.kommand.kommand
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class RegionSurvivalPlugin : JavaPlugin() {
    lateinit var landManager: LandManager
    override fun onEnable() {
        dataFolder.mkdirs()
        setupServer()
        setupModules()
    }
    fun setupServer() {
        Bukkit.getWorlds().first().let {
            val y = it.getHighestBlockAt(0, 0).location.y + 1.0
            it.setSpawnLocation(0, y.toInt(), 0)
            it.worldBorder.setCenter(0.0, 0.0)
            it.worldBorder.size = 49.0 * 49.0
        }
    }

    fun setupModules() {
        Bukkit.getPluginManager().registerEvents(RegionListener(), this)
        landManager = LandManager(this).apply {
            loadLand()
        }

        kommand {
            RegionKommand.register(this)
        }
    }
}