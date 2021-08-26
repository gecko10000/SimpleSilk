package io.github.levtey.SimpleSilk;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.persistence.PersistentDataType;

public class Listeners implements Listener {
	
	private final SimpleSilk plugin;
	private final Set<UUID> confirmations = new HashSet<>();
	
	public Listeners(SimpleSilk plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void on(BlockPlaceEvent evt) {
		ItemStack item = evt.getItemInHand();
		if (item.getType() != Material.SPAWNER) return;
		BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();
		CreatureSpawner itemSpawner = (CreatureSpawner) meta.getBlockState();
		CreatureSpawner spawner = (CreatureSpawner) evt.getBlock().getState();
		spawner.setSpawnedType(itemSpawner.getSpawnedType());
		spawner.getPersistentDataContainer().set(SimpleSilk.CUSTOM_KEY, PersistentDataType.BYTE, (byte) 0);
		spawner.setDelay(spawner.getMaxSpawnDelay()/2);
		spawner.update();
	}
	
	@EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void on(BlockBreakEvent evt) {
		Player player = evt.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();
		Block block = evt.getBlock();
		if (block.getType() != Material.SPAWNER) return;
		CreatureSpawner spawner = (CreatureSpawner) block.getState();
		if (!player.hasPermission("ss.silk") || item == null || !isPickaxe(item.getType()) || spawner.getPersistentDataContainer().isEmpty() || item.getEnchantmentLevel(Enchantment.SILK_TOUCH) <= 0) {
			UUID uuid = player.getUniqueId();
			if (confirmations.remove(uuid)) {
				return;
			}
			confirmations.add(uuid);
			player.sendMessage(SimpleSilk.makeReadable(plugin.getConfig().getString("no-silk")));
			Bukkit.getScheduler().runTaskLater(plugin, () -> confirmations.remove(uuid), plugin.getConfig().getInt("time") * 20);
			evt.setCancelled(true);
			return;
		}
		evt.setExpToDrop(0);
		evt.setDropItems(false);
		Location dropLoc = block.getLocation();
		dropLoc.getWorld().dropItemNaturally(dropLoc, plugin.getSpawnerItem(spawner.getSpawnedType()));
	}
	
	private boolean isPickaxe(Material m) {
		return m.toString().endsWith("_PICKAXE");
	}

}
