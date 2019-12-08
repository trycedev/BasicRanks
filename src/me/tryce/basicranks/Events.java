package me.tryce.basicranks;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Events implements Listener{ 
	
	private Main plugin = Main.getPlugin(Main.class);
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void join(PlayerJoinEvent e) {
		plugin.fixRemovedRanks();
		Player p = e.getPlayer();
		File localeDir = new File(plugin.getDataFolder(), "data");
	    if (!localeDir.exists()) {
	        localeDir.mkdirs();
	    }
		File file = new File(plugin.getDataFolder()+File.separator+"data", "players.yml");
		FileConfiguration players = YamlConfiguration.loadConfiguration(file);
		if(!file.exists() || file == null) {
			try {
				file.createNewFile();
				
				players.save(file);	
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		if(!players.contains("Players." + p.getUniqueId())) {
			players.set("Players." + p.getUniqueId(), plugin.getConfig().getString("default-rank"));
		}
		try {
			players.save(file);	
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		plugin.setupPerms(p);
	}
	@EventHandler
	public void quit(PlayerQuitEvent e) {
		plugin.fixRemovedRanks();
		Player p = e.getPlayer();
		p.removeAttachment(plugin.playerPerms.get(p.getUniqueId()));
	}
	@EventHandler (priority = EventPriority.HIGHEST)
	public void chat(AsyncPlayerChatEvent e) {
		plugin.fixRemovedRanks();
		if(plugin.getConfig().getBoolean("use-chat")) {
			e.setCancelled(true);
			File file = new File(plugin.getDataFolder()+File.separator+"data", "players.yml");
			FileConfiguration players = YamlConfiguration.loadConfiguration(file);
			
			File file2 = new File(plugin.getDataFolder()+File.separator+"data", "ranks.yml");
			FileConfiguration ranks = YamlConfiguration.loadConfiguration(file2);
			
			String rank = players.getString("Players." + e.getPlayer().getUniqueId());
			
			String format = ChatColor.translateAlternateColorCodes('&', ranks.getString("Ranks." + rank + ".chatFormat"));
			
			if(e.getPlayer().isOp() || e.getPlayer().hasPermission("chat.color")) {
				e.setMessage(ChatColor.translateAlternateColorCodes('&', e.getMessage()));
			}
			format = format.replaceAll("%rank%", rank);
			format = format.replaceAll("%player%", e.getPlayer().getName());
			format = format.replaceAll("%msg%", e.getMessage());
			
			Bukkit.broadcastMessage(format);
		}
	}
}
