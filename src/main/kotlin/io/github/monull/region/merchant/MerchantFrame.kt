package io.github.monull.region.merchant

import io.github.monull.region.Lands
import io.github.monun.invfx.InvFX
import io.github.monun.invfx.frame.InvFrame
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

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
    fun openFrame(): InvFrame {
        return InvFX.frame(3, Component.text("부동산")) {
            list(1, 0, 7, 0, false, { Lands.lands }) {
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
}