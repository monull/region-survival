package io.github.monull.region

import io.github.monull.region.land.Land
import io.github.monull.region.merchant.Merchant
import io.github.monun.kommand.PluginKommand
import io.github.monun.kommand.getValue
import io.github.monun.kommand.node.RootNode
import org.bukkit.entity.Player

object RegionKommand {
    fun register(kommand: PluginKommand) {
        kommand.register("region") {
            val lands = dynamic { context, input ->
                Lands.lands.find { it.name == "$input.yml" }
            }.apply {
                suggests {
                    suggest(Lands.lands.map { it.name.removeSuffix(".yml") })
                }
            }
            then("setowner") {
                then("land" to lands) {
                    then("player" to player()) {
                        executes { context ->
                            val player: Player by context
                            val land: Land by context
                            land.owner = player.name
                            land.save()
                        }
                    }
                }
            }
            then("setinitial") {
                then("land" to lands) {
                    then("player" to player()) {
                        executes {
                            val player: Player by it
                            val land: Land by it
                            Lands.findPlayer(player).initialLand = land
                            land.owner = player.name
                            land.save()
                        }
                    }
                }
            }
            then("start") {
                executes {
                    Lands.canAccessAll = false
                    setupLands()
                }
            }
            then("stop") {
                executes {
                    Lands.canAccessAll = true
                }
            }
        }
    }

    private fun setupLands() {
        Lands.merchantPlayers.forEach {
            val loc = it.initialLand.loc
            it.merchant = Merchant(it.player).apply {
                initialize(Lands.plugin, loc)
            }
        }
    }
}