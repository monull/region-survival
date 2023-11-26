package io.github.monull.region.land

import io.github.monun.tap.config.Config
import io.github.monun.tap.config.ConfigSupport
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class Land {

    var locx = 0.0

    var locy = 0.0

    var locz = 0.0

    var loc = Location(Bukkit.getWorlds().first(), locx, locy, locz)

    var name = "$locx$locz.yml"

    var price = 10

    var owner = "null"

    lateinit var file: File

    fun load(file: File) {
        this.file = file
        val yaml = YamlConfiguration.loadConfiguration(file)
        locx = yaml.getDouble("locx")
        locy = yaml.getDouble("locy")
        locz = yaml.getDouble("locz")
        loc = Location(Bukkit.getWorlds().first(), locx, locy, locz)
        name = "$locx$locz.yml"
        price = yaml.getInt("price")
        owner = yaml.getString("owner")!!
    }

    fun save() {
        val yaml = YamlConfiguration()
        yaml.set("locx", locx)
        yaml.set("locy", locy)
        yaml.set("locz", locz)
        yaml.set("owner", owner)
        yaml.set("price", price)
        yaml.save(file)
    }

}