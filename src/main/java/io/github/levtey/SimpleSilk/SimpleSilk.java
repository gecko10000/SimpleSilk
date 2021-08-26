package io.github.levtey.SimpleSilk;

import java.util.StringJoiner;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class SimpleSilk extends JavaPlugin {
	
	public static final NamespacedKey CUSTOM_KEY = NamespacedKey.fromString("simplesilk:placed");
	
	public void onEnable() {
		saveDefaultConfig();
		new CommandHandler(this);
		new Listeners(this);
	}
	
	@SuppressWarnings("deprecation")
	public ItemStack getSpawnerItem(EntityType entity) {
		ItemStack item = new ItemStack(Material.SPAWNER);
		BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();
		meta.setDisplayName(makeReadable(getConfig().getString("spawner-name")
				.replace("%type%", format(entity.getName()))));
		CreatureSpawner spawner = (CreatureSpawner) meta.getBlockState();
		spawner.setSpawnedType(entity);
		meta.setBlockState(spawner);
		item.setItemMeta(meta);
		return item;
	}
	
	public static String makeReadable(String input) {
		return ChatColor.translateAlternateColorCodes('&', input);
	}
	
	public static String format(String input) {
		StringJoiner joiner = new StringJoiner(" ");
		for (String word : input.split("_")) {
			joiner.add(word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase());
		}
		return joiner.toString();
	}

}
