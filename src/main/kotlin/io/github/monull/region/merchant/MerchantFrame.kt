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
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

object MerchantFrame {
    private val previousItem =
        ItemStack(Material.END_CRYSTAL).apply {
            itemMeta = itemMeta.apply {
                displayName(
                    text().color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .decorate(TextDecoration.BOLD).content("←").build()
                )
            }
        }
    private val nextItem =
        ItemStack(Material.END_CRYSTAL).apply {
            itemMeta = itemMeta.apply {
                displayName(
                    text().color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .decorate(TextDecoration.BOLD).content("→").build()
                )
            }
        }

    fun openMenuFrame(player: MerchantPlayer): InvFrame {
        return InvFX.frame(1, text("메뉴")) {
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
            list(1, 1, 7, 4, false, { Lands.lands }) {
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
                    if (land.owner == "null" && player.player.inventory.contains(Material.DIAMOND, land.price)) {
                        player.player.inventory.remove(ItemStack(Material.DIAMOND, land.price))
                        land.owner = player.player.name
                    }
                }
            }.let { list ->
                slot(0, 0) {
                    item = previousItem
                    onClick {
                        list.index--
                    }
                }
                slot(8, 0) {
                    item = nextItem
                    onClick {
                        list.index++
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
                    land.owner = "null"
                    player.player.inventory.addItem(ItemStack(Material.DIAMOND, land.price))
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

                onClickItem { x, y, (player, item), event ->
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