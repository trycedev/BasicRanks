package me.tryce.basicranks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.tryce.basicranks.api.Api;

public class Main extends JavaPlugin{

	public HashMap<UUID, PermissionAttachment> playerPerms = new HashMap<>();

	public void onEnable() {
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN + this.getName() + " has been loaded!");
		getServer().getPluginManager().registerEvents(new Events(), this);
		getCommand("basicranks").setExecutor(new CMD());

		File localeDir = new File(getDataFolder(), "data");
		if (!localeDir.exists()) {
			localeDir.mkdirs();
		}
		for(Player p : Bukkit.getOnlinePlayers()) {
			setupPerms(p);
		}

		File file = new File(getDataFolder()+File.separator+"data", "ranks.yml");
		FileConfiguration ranks = YamlConfiguration.loadConfiguration(file);
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
		if(!file.exists() || file == null) {

			try {

				file.createNewFile();

				ranks.set("Ranks.Admin.chatFormat", "&4[%rank%] %player%&8: &7%msg%");
				ArrayList<String> perms = new ArrayList<String>();
				perms.add("rank.Admin");
				ranks.set("Ranks.Admin.permissions", perms);
				ArrayList<String> inherit = new ArrayList<String>();
				ranks.set("Ranks.Admin.inherits", inherit);

				
				ranks.set("Ranks.Default.chatFormat", "&7%player%&8: &7%msg%");
				perms = new ArrayList<String>();
				ranks.set("Ranks.Default.permissions", perms);
				inherit = new ArrayList<String>();
				ranks.set("Ranks.Default.inherits", inherit);

				ranks.save(file);	

				return;
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}	

		new BukkitRunnable() {

			@Override
			public void run() {
				for(Player p : Bukkit.getOnlinePlayers()) {
					updatePerms(p);
				}
			}
		}.runTaskTimer(this, 0, 20 * 1);

		checkDefaultRank();
	}
	private void checkDefaultRank() {
		if(!Api.getAllRanks().contains(getConfig().getString("default-rank"))) {
			Bukkit.getPluginManager().disablePlugin(this);
			getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "" + this.getName() + " has met an error!");
			getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "");
			getServer().getConsoleSender().sendMessage("§4§oRanks.yml does not contain \"default-rank\"");
		}

	}
	public void fixRemovedRanks() {
		File playersFile = new File(getDataFolder()+File.separator+"data", "players.yml");
		FileConfiguration playersConfig = YamlConfiguration.loadConfiguration(playersFile);

		if(!playersFile.exists() || playersFile == null) {
			try {
				playersFile.createNewFile();

				playersConfig.save(playersFile);	
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		if(playersConfig.contains("Players")) {
			for(String uuid : playersConfig.getConfigurationSection("Players").getKeys(false)) {
				if(!Api.rankExists(playersConfig.getString("Players." + uuid))) {
					playersConfig.set("Players." + uuid, getConfig().getString("default-rank"));
					try {	
						playersConfig.save(playersFile);	
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}

	}
	public void onDisable() {
		getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + this.getName() + " has been unloaded!");
		for(Player p : Bukkit.getOnlinePlayers()) {
			p.removeAttachment(playerPerms.get(p.getUniqueId()));
		}
		playerPerms.clear();
	}
	public void setupPerms(Player p) {
		PermissionAttachment attachment = p.addAttachment(this);
		this.playerPerms.put(p.getUniqueId(), attachment);

	}
	public void updatePerms(Player p) {
		File file = new File(getDataFolder()+File.separator+"data", "ranks.yml");
		FileConfiguration ranksfile = YamlConfiguration.loadConfiguration(file);

		File file2 = new File(getDataFolder()+File.separator+"data", "players.yml");
		FileConfiguration players = YamlConfiguration.loadConfiguration(file2);

		String playerRank = players.getString("Players." + p.getUniqueId());

		for(String perm : playerPerms.get(p.getUniqueId()).getPermissions().keySet()) {
			playerPerms.get(p.getUniqueId()).unsetPermission(perm);
		}
		for(String rank : ranksfile.getConfigurationSection("Ranks").getKeys(false)) {
			if(playerRank.equals(rank)) {
				for(String perm : ranksfile.getStringList("Ranks." + playerRank + ".permissions")) {
					playerPerms.get(p.getUniqueId()).setPermission(perm, true);
				}
				for(String inherits : ranksfile.getStringList("Ranks." + playerRank + ".inherits")) {
					for(String perm : ranksfile.getStringList("Ranks." + inherits + ".permissions")) {
						playerPerms.get(p.getUniqueId()).setPermission(perm, true);
					}
				}
			}
		}
	}
	public boolean checkRank(UUID uuid, String rank) {
		File file = new File(getDataFolder()+File.separator+"data", "players.yml");
		FileConfiguration players = YamlConfiguration.loadConfiguration(file);
		if(players.getString("Players." + uuid).equals(rank)) {
			return true;
		}
		return false;
	}
	public boolean isInt(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}
	public boolean isFloat(String s) {
		try {
			Float.parseFloat(s);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

}
