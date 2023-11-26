package io.github.monull.region.plugin

import io.github.monull.region.Merchant
import io.github.monull.region.RegionKommand
import io.github.monull.region.land.LandManager
import io.github.monun.kommand.kommand
import io.github.monun.tap.fake.FakeEntityServer
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin

class RegionSurvivalPlugin : JavaPlugin() {
    lateinit var landManager: LandManager
    lateinit var fakeEntityServer: FakeEntityServer
    lateinit var merchant: Merchant
    override fun onEnable() {
        dataFolder.mkdirs()
        setupServer()
        setupModules()
    }

    override fun onDisable() {
        landManager.saveLand()
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

        fakeEntityServer = FakeEntityServer.create(this).apply {
            Bukkit.getPluginManager().registerEvents(object : Listener {
                @EventHandler
                fun onPlayerJoin(event: PlayerJoinEvent) {
                    addPlayer(event.player)
                }
                @EventHandler
                fun onPlayerQuit(event: PlayerQuitEvent) {
                    removePlayer(event.player)
                }
            }, this@RegionSurvivalPlugin)
            Bukkit.getScheduler().runTaskTimer(this@RegionSurvivalPlugin, Runnable { update() }, 0L, 1L)
        }
        merchant = Merchant().apply { initialize(this@RegionSurvivalPlugin) }
    }
}