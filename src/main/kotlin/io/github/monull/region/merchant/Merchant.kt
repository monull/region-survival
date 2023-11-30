package io.github.monull.region.merchant

import io.github.monull.region.Lands
import io.github.monull.region.plugin.RegionSurvivalPlugin
import io.github.monun.invfx.openFrame
import io.github.monun.tap.fake.FakeEntity
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.Interaction
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEntityEvent

class Merchant(val player: Player) {
    lateinit var entity: FakeEntity<Player>
    lateinit var interactioner: Entity
    lateinit var listener: Listener
    fun initialize(plugin: RegionSurvivalPlugin, loc: Location) {
        val bl = Bukkit.getWorlds().first().getHighestBlockAt(loc.x.toInt(), loc.z.toInt())
        val loc2 = Location(Bukkit.getWorlds().first(), loc.x, bl.y.toDouble() + 1.0, loc.z)
        entity = plugin.fakeEntityServer.spawnPlayer(loc2, player.name)
        interactioner = Bukkit.getWorlds().first().spawn(loc2, Interaction::class.java).apply {
            this.interactionHeight = 2.0F
            this.interactionWidth = 1.0F
        }
        listener = MerchantListener()
        Bukkit.getPluginManager().registerEvents(listener, plugin)
    }

    inner class MerchantListener : Listener {
        @EventHandler
        fun onPlayerInteractEntity(event: PlayerInteractEntityEvent) {
            if (event.rightClicked == interactioner) {
                event.player.openFrame(MerchantFrame.openFrame())
            }
        }
    }
}