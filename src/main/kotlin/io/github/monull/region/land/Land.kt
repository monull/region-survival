package io.github.monull.region.land

import io.github.monun.tap.config.Config
import io.github.monun.tap.config.ConfigSupport
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File

class Land {

    var locx = 0.0

    var locy = 0.0

    var locz = 0.0

    var loc = Location(Bukkit.getWorlds().first(), locx, locy, locz)

    var name = "$locx$locz.yml"

    var price = 10

    var owner = "null"

    var whitelist = arrayListOf<String>()

    lateinit var file: File

    lateinit var box: LandBox

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
        yaml.getStringList("whitelist").forEach {
            whitelist += it
        }
    }

    fun save() {
        val yaml = YamlConfiguration()
        yaml.set("locx", locx)
        yaml.set("locy", locy)
        yaml.set("locz", locz)
        yaml.set("owner", owner)
        yaml.set("price", price)
        yaml.set("whitelist", whitelist)
        yaml.save(file)
    }

    fun setBox() {
        box = LandBox(locx - 4.5, locz - 4.5, locx + 4.5, locz + 4.5)
    }

}