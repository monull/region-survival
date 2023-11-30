package io.github.monull.region.merchant

import io.github.monull.region.Lands
import io.github.monull.region.land.Land
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File

class MerchantPlayer(val player: Player) {
    var initialLand: Land = Lands.nullLand
    var merchant: Merchant? = null

    fun save() {
        val plugin = Lands.plugin
        val file = File(plugin.dataFolder, "${player.name}.yml")
        val yaml = YamlConfiguration()
        yaml.set("landsx", initialLand.loc.x)
        yaml.set("landz", initialLand.loc.z)
        yaml.save(file)
    }

    fun load(file: File) {
        val yaml = YamlConfiguration.loadConfiguration(file)
        initialLand = Lands.lands.find { it.locx == yaml["landsx"] && it.locz == yaml["landz"]}!!
    }
}