package io.github.monull.region.merchant

import io.github.monull.region.Lands
import io.github.monull.region.land.Land
import io.github.monun.invfx.InvFX
import io.github.monun.invfx.frame.InvFrame
import io.github.monun.invfx.openFrame
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

class MerchantFrame {
    private val previousItem =
        ItemStack(Material.END_CRYSTAL).apply {
            itemMeta = itemMeta.apply {
                displayName(
                    text().color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .decorate(TextDecoration.BOLD).content("↑").build()
                )
            }
        }
    private val nextItem =
        ItemStack(Material.END_CRYSTAL).apply {
            itemMeta = itemMeta.apply {
                displayName(
                    text().color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .decorate(TextDecoration.BOLD).content("↓").build()
                )
            }
        }

    fun openMenuFrame(player: MerchantPlayer): InvFrame {
        return InvFX.frame(1, text("☛")) {
            slot(0, 0) {
                item = ItemStack(Material.GRASS_BLOCK).apply {
                    itemMeta = itemMeta.apply {
                        displayName(
                            text("시작 땅: ").color(NamedTextColor.GREEN)
                        )
                        lore(
                            listOf(
                                text().color(NamedTextColor.BLUE).decoration(TextDecoration.ITALIC, false)
                                    .content("좌표: ").append(text().color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false).content(player.initialLand.name.removeSuffix(".yml")).build()).build(),
                                text().color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false)
                                    .content("땅값: ").append(text().color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false).content(player.initialLand.price.toString())).build()
                            )
                        )
                    }
                }
            }

            slot(3, 0) {
                item = ItemStack(Material.RED_WOOL).apply {
                    itemMeta = itemMeta.apply {
                        displayName(text("땅 구매하기"))
                    }
                }

                onClick {
                    player.player.openFrame(openLandsFrame(player))
                }
            }

            slot(4, 0) {
                item = ItemStack(Material.YELLOW_WOOL).apply {
                    itemMeta = itemMeta.apply {
                        displayName(text("내 땅 보기"))
                    }
                }

                onClick {
                    player.player.openFrame(openMyLandsFrame(player))
                }
            }

            slot(5, 0) {
                item = ItemStack(Material.GREEN_WOOL).apply {
                    itemMeta = itemMeta.apply {
                        displayName(text("땅 검색하기"))
                    }
                }
            }
        }
    }

    fun openLandsFrame(player: MerchantPlayer): InvFrame {
        return InvFX.frame(5, text("부동산")) {
            for (y in 1..3) {
                slot(8, y) {
                    item = ItemStack(Material.YELLOW_WOOL)
                }
            }
            list(0, 0, 7, 4, false, { Lands.lands }) {
                transform {
                    ItemStack(Material.GRASS_BLOCK).apply {
                        itemMeta = itemMeta.apply {
                            displayName(text("(${it.locx.toInt()}, ${it.locz.toInt()})"))
                            lore(
                                listOf<Component>(
                                    text().color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false)
                                        .content("땅값: ").build().append(text().color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                                            .content("${it.price}").build()),
                                    text().color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false).content("주인: ").build()
                                        .append(text().color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false).content(
                                            if (it.owner == "null") "없음" else it.owner
                                        ).build())
                                )
                            )
                            if (it.owner != "null") {
                                this.addEnchant(Enchantment.KNOCKBACK, 0, false)
                            }
                        }
                    }
                }

                onClickItem { x, y, (land, _), event ->
                    val item = event.currentItem!!
                    if (land.owner == "null" && player.player.inventory.contains(Material.DIAMOND, land.price)) {
                        player.player.inventory.remove(ItemStack(Material.DIAMOND, land.price))
                        land.owner = player.player.name
                        player.player.sendMessage(text("(${land.locx}, ${land.locz})에 있는 땅이 당신의 땅이 되었습니다."))
                        item.itemMeta = item.itemMeta.apply {
                            displayName(text("(${land.locx.toInt()}, ${land.locz.toInt()})"))
                            lore(
                                listOf<Component>(
                                    text().color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false)
                                        .content("땅값: ").build().append(text().color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                                            .content("${land.price}").build()),
                                    text().color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false).content("주인: ").build()
                                        .append(text().color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false).content(
                                            if (land.owner == "null") "없음" else land.owner
                                        ).build())
                                )
                            )
                            if (land.owner != "null") {
                                this.addEnchant(Enchantment.KNOCKBACK, 0, false)
                            }
                        }
                    }
                }
            }.let { list ->
                slot(8, 0) {
                    item = previousItem
                    onClick {
                        list.index -= 8
                    }
                }
                slot(8, 4) {
                    item = nextItem
                    onClick {
                        list.index += 8
                    }
                }
            }
        }
    }

    fun openMyLandsFrame(player: MerchantPlayer): InvFrame {
        return InvFX.frame(3, text("내 땅")) {
            list(1, 0, 7, 0, false, { findMyLands(player) }) {
                transform {
                    ItemStack(Material.GRASS_BLOCK).apply {
                        itemMeta = itemMeta.apply {
                            displayName(text("(${it.locx.toInt()}, ${it.locz.toInt()})"))
                            lore(
                                listOf<Component>(
                                    text().color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false)
                                        .content("땅값: ").build().append(text().color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                                            .content("${it.price}").build()),
                                    text().color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false).content("주인: ").build()
                                        .append(text().color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false).content(
                                            if (it.owner == "null") "없음" else it.owner
                                        ).build())
                                )
                            )
                        }
                    }
                }

                onClickItem { x, y, (land, item), event ->
                    player.player.openFrame(openMyLandSettings(player, land))
                }
            }
        }
    }

    fun openMyLandSettings(player: MerchantPlayer, land: Land): InvFrame {
        return InvFX.frame(1, text(land.name.removeSuffix(".yml"))) {
            slot(0, 0) {
                item = ItemStack(Material.COMPASS).apply {
                    itemMeta = itemMeta.apply {
                        displayName(text("내 땅으로 이동하기"))
                    }
                }

                onClick {
                    player.player.sendMessage(text("순간이동!"))
                    player.player.teleport(Location(Bukkit.getWorlds().first(), land.locx, Bukkit.getWorlds().first().getHighestBlockAt(land.locx.toInt(), land.locz.toInt()).location.y, land.locz))
                }
            }

            slot(1, 0) {
                item = ItemStack(Material.COMPASS).apply {
                    itemMeta = itemMeta.apply {
                        displayName(text("땅 팔기"))
                    }
                }

                onClick {
                    if (land == player.initialLand) {
                        player.player.sendMessage(text("초기 땅은 팔 수 없습니다!"))
                    } else {
                        land.owner = "null"
                        player.player.inventory.addItem(ItemStack(Material.DIAMOND, land.price))
                        player.player.sendMessage(text("(${land.locx}, ${land.locz})에 있는 땅을 팔았습니다."))
                        player.player.closeInventory()
                    }
                }
            }

            slot(2, 0) {
                item = ItemStack(Material.PLAYER_HEAD).apply {
                    itemMeta = itemMeta.apply {
                        displayName(text("화이트 리스트"))
                    }
                }

                onClick {
                    player.player.openFrame(openWhiteListFrame(player, land))
                }
            }
        }
    }

    fun openWhiteListFrame(player: MerchantPlayer, land: Land): InvFrame {
        return InvFX.frame(2, text("화이트 리스트")) {
            list(1, 0, 7, 0, false, { Bukkit.getOnlinePlayers().filter { it.name != player.player.name }}) {
                transform {
                    ItemStack(Material.PLAYER_HEAD).apply {
                        itemMeta = (itemMeta as SkullMeta).apply {
                            displayName(text(it.name))
                            this.setOwningPlayer(Bukkit.getOfflinePlayer(it.name))
                            lore(
                                listOf(
                                    if (land.whitelist.contains(it.name)) {
                                        text("친구").color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false)
                                    } else text("남").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false)
                                )
                            )
                        }
                    }
                }

                onClickItem { x, y, (player, _), event ->
                    val item = event.currentItem!!
                    if (land.whitelist.contains(player.name)) {
                        land.whitelist.remove(player.name)
                        item.itemMeta = (item.itemMeta as SkullMeta).apply {
                            displayName(text(player.name))
                            this.setOwningPlayer(Bukkit.getOfflinePlayer(player.name))
                            lore(
                                listOf(
                                    if (land.whitelist.contains(player.name)) {
                                        text("친구").color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false)
                                    } else text("남").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false)
                                )
                            )
                        }
                    } else {
                        land.whitelist.add(player.name)
                        item.itemMeta = (item.itemMeta as SkullMeta).apply {
                            displayName(text(player.name))
                            this.setOwningPlayer(Bukkit.getOfflinePlayer(player.name))
                            lore(
                                listOf(
                                    if (land.whitelist.contains(player.name)) {
                                        text("친구").color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false)
                                    } else text("남").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false)
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    fun findMyLands(player: MerchantPlayer): List<Land> {
        return Lands.lands.filter { it.owner == player.player.name }
    }
}