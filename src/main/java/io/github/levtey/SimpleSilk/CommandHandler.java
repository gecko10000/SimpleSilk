package io.github.levtey.SimpleSilk;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandHandler implements CommandExecutor {
	
	private final SimpleSilk plugin;
	private static final String NO_PERMS = "&cYou don't have permission!";
	private static final String INVALID_ENTITY = "&cInvalid entity type.";
	private static final String INVALID_NUMBER = "&cInvalid amount.";
	private static final String RELOAD = "&aConfig reloaded!";
	
	public CommandHandler(SimpleSilk plugin) {
		this.plugin = plugin;
		plugin.getCommand("simplesilk").setExecutor(this);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("ss.admin")) return response(sender, NO_PERMS);
		if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
			plugin.saveDefaultConfig();
			plugin.reloadConfig();
			return response(sender, RELOAD);
		}
		if (args.length > 2 && args[0].equalsIgnoreCase("give")) {
			Player player = Bukkit.getPlayer(args[1]);
			try {
				EntityType type = EntityType.valueOf(args[2].toUpperCase());
				int amount = args.length > 3 ? Integer.parseInt(args[3]) : 1;
				ItemStack spawnerItem = plugin.getSpawnerItem(type);
				spawnerItem.setAmount(amount);
				player.getInventory().addItem(spawnerItem);
			} catch (NumberFormatException e) {
				return response(sender, INVALID_NUMBER);
			} catch (IllegalArgumentException e) {
				return response(sender, INVALID_ENTITY);
			}
		}
		return true;
	}
	
	private boolean response(CommandSender sender, String message) {
		sender.sendMessage(SimpleSilk.makeReadable(message));
		return true;
	}

}
