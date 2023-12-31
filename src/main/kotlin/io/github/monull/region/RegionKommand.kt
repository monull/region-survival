package io.github.monull.region

import io.github.monull.region.land.Land
import io.github.monull.region.merchant.Merchant
import io.github.monull.region.merchant.MerchantFrame
import io.github.monun.kommand.PluginKommand
import io.github.monun.kommand.getValue
import net.kyori.adventure.text.Component.text
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
                            feedback(text("(${land.locx}, ${land.locz})에 있는 땅이 ${player.name}의 땅이 되었습니다."))
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
                            feedback(text("(${land.locx}, ${land.locz})에 있는 땅이 ${player.name}의 시작 땅이 되었습니다."))
                        }
                    }
                }
            }
            then("start") {
                requires {
                    var x = true
                    Lands.merchantPlayers.forEach {
                        if (it.initialLand == Lands.nullLand) x = false
                    }
                    x
                }
                executes {
                    Lands.canAccessAll = false
                    setupLands()
                    Lands.merchantPlayers.forEach {
                        it.player.teleport(it.merchant!!.entity.location)
                    }
                    feedback(text("시작!"))
                }
            }
            then("stop") {
                executes {
                    Lands.canAccessAll = true
                    feedback(text("끝!"))
                    Lands.merchantPlayers.forEach {
                        it.merchant?.interactioner?.remove()
                        it.save()
                    }
                    Lands.plugin.fakeEntityServer.entities.forEach {
                        it.remove()
                    }
                }
            }

            then("test") {
                executes {
                    MerchantFrame().trade(Lands.merchantPlayers.first(), Lands.merchantPlayers.first())
                }
            }
        }
    }

    private fun setupLands() {
        Lands.merchantPlayers.forEach {
            val loc = it.initialLand.loc
            it.merchant = Merchant(it).apply {
                initialize(Lands.plugin, loc)
            }
            Lands.merchants.add(it.merchant!!)
        }
    }
}