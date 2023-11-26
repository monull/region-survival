package io.github.monull.region

import io.github.monull.region.plugin.RegionSurvivalPlugin
import io.github.monun.tap.fake.FakeEntity
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.Interaction
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractAtEntityEvent

class Merchant {
    lateinit var entity: FakeEntity<Player>
    lateinit var interactioner: Entity
    lateinit var listener: Listener
    fun initialize(plugin: RegionSurvivalPlugin) {
        val bl = Bukkit.getWorlds().first().getHighestBlockAt(0, 0)
        val loc = Location(Bukkit.getWorlds().first(), 0.0, bl.y.toDouble() + 1.0, 0.0)
        entity = plugin.fakeEntityServer.spawnPlayer(loc, "xenon542")
        interactioner = Bukkit.getWorlds().first().spawn(loc, Interaction::class.java).apply {
            this.interactionHeight = 2.0F
            this.interactionWidth = 1.0F
        }
        listener = MerchantListener()
        Bukkit.getPluginManager().registerEvents(listener, plugin)
    }

    inner class MerchantListener : Listener {
        @EventHandler
        fun onPlayerInteract(event: PlayerInteractAtEntityEvent) {
            if (event.rightClicked == interactioner) {
                println("hi")
                event.isCancelled = true
            }
        }
    }
}