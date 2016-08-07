package com.shmozo.slither.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;

public class ItemBuilder {

	private ItemStack itemStack;

	public ItemBuilder setItem(ItemStack item, String name, String[] lore) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(Arrays.asList(lore));
		item.setItemMeta(meta);
		this.itemStack = item;
		return this;
	}

	public ItemBuilder setItem(Material material, short shortID, String name, String[] lore) {
		ItemStack tempItem = new ItemStack(material, 1, shortID);
		ItemMeta meta = tempItem.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(Arrays.asList(lore));
		tempItem.setItemMeta(meta);
		this.itemStack = tempItem;
		return this;
	}

	public ItemBuilder setItem(ItemStack item) {
		this.itemStack = item;
		return this;
	}

	public ItemBuilder createSkull(String playerName) {
		ItemStack tempItem = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta meta = (SkullMeta) tempItem.getItemMeta();
		meta.setOwner(playerName);
		tempItem.setItemMeta(meta);
		this.itemStack = tempItem;
		return this;
	}

	public ItemBuilder addLore(String lore) {
		ItemStack item = itemStack;
		ItemMeta meta = item.getItemMeta();
		List<String> itemLore = meta.getLore();
		itemLore.add(lore);
		meta.setLore(itemLore);
		item.setItemMeta(meta);
		this.itemStack = item;
		return this;
	}

	public ItemStack build() {
		return itemStack;
	}

}
