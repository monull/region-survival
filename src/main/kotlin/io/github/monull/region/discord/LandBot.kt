package io.github.monull.region.discord

import io.github.monull.region.nearestLand
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player

class LandBot : ListenerAdapter() {
    val players = HashMap<User, Player>()
    fun start() {
        val bot = JDABuilder.createDefault("---M---T-E4NTkwMDA3MjIzNjQ4MjU3MA.G1Im-E.IPfNjBdFMbotYmdAmKR7D9_aMsxJ3HMX8Bx0OI")
            .addEventListeners(this).build()

        val commands = bot.updateCommands()

        commands.addCommands(
            Commands.slash("register", "유저 등록")
                .addOptions(OptionData(OptionType.USER, "user", "discord user")
                    .setRequired(true))
                .addOptions(OptionData(OptionType.STRING, "nick", "minecraft nickname")
                    .setRequired(true)),
            Commands.slash("region", "Commands for Region Survival")
                .addOptions(OptionData(OptionType.USER, "user", "select the user")
                    .setRequired(true))
                .addOptions(OptionData(OptionType.INTEGER, "x", "land location.x")
                    .setRequired(true))
                .addOptions(OptionData(OptionType.INTEGER, "z", "land location.z")
                    .setRequired(true))
        )

        commands.queue()
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        when (event.name) {
            "register" -> {
                val user = event.getOption("user")?.asUser!!
                val nick = event.getOption("nick")?.asString!!
                players[user] = Bukkit.getOnlinePlayers().find { it.name == nick }!!
                event.reply("${user.name}님의 마크 닉네임을 ${nick}으로 설정하였습니다.")
            }
            "region" -> {
                val user = event.getOption("user")!!.asUser
                val x = event.getOption("x")!!.asInt
                val z = event.getOption("z")!!.asInt
                Location(Bukkit.getWorlds().first(), x.toDouble(), 0.0, z.toDouble()).nearestLand.owner = players[user]?.name!!
                event.reply("(${x}, ${z})에 있는 땅이 ${user.name}님의 땅이 되었습니다.")
            }
        }
    }
}