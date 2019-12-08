package me.tryce.basicranks.api;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.tryce.basicranks.Main;

public class Api {
	
	private static Main plugin = Main.getPlugin(Main.class);
	
	public static boolean rankExists(String rankname) {
		File file2 = new File(plugin.getDataFolder()+File.separator+"data", "ranks.yml");
		FileConfiguration ranks = YamlConfiguration.loadConfiguration(file2);
		for(String rank : ranks.getConfigurationSection("Ranks").getKeys(false)) {
			if(rankname.equals(rank)) {
				return true;
			}
		}
		return false;
	}
	public static boolean isDefault(String rank) {
		return plugin.getConfig().getString("default-rank").equals(rank);
	}
	public static boolean hasPermission(String rank, String perm) {
		File ranksFile = new File(plugin.getDataFolder()+File.separator+"data", "ranks.yml");
		FileConfiguration ranksConfig = YamlConfiguration.loadConfiguration(ranksFile);
		
		return ranksConfig.getStringList("Ranks."+rank+".permissions").contains(perm);
	}
	public static boolean hasInherit(String rank, String inherit) {
		File ranksFile = new File(plugin.getDataFolder()+File.separator+"data", "ranks.yml");
		FileConfiguration ranksConfig = YamlConfiguration.loadConfiguration(ranksFile);
		
		return ranksConfig.getStringList("Ranks."+rank+".inherits").contains(inherit);
	}
	public static List<String> getAllRanks() {
		File file2 = new File(plugin.getDataFolder()+File.separator+"data", "ranks.yml");
		FileConfiguration ranks = YamlConfiguration.loadConfiguration(file2);
		ArrayList<String> allranks = new ArrayList<String>();
		for(String rank : ranks.getConfigurationSection("Ranks").getKeys(false)) {
			allranks.add(rank);
		}
		
		return allranks;
	}
	
	public static String getPlayerFormat(Player p) {
		File file = new File(plugin.getDataFolder()+File.separator+"data", "players.yml");
		FileConfiguration players = YamlConfiguration.loadConfiguration(file);
		String rank = players.getString("Players." + p.getUniqueId());
		File file2 = new File(plugin.getDataFolder()+File.separator+"data", "ranks.yml");
		FileConfiguration ranks = YamlConfiguration.loadConfiguration(file2);
		
		return ranks.getString("Ranks." + rank + ".chatFormat");
	}
	
	public static List<String> getRankPermissions(String rank) {
		File file2 = new File(plugin.getDataFolder()+File.separator+"data", "ranks.yml");
		FileConfiguration ranks = YamlConfiguration.loadConfiguration(file2);
		
		return ranks.getStringList("Ranks." + rank + ".permissions");
	}
	
	public static List<String> getPlayerPermissions(Player p) {
		File file = new File(plugin.getDataFolder()+File.separator+"data", "players.yml");
		FileConfiguration players = YamlConfiguration.loadConfiguration(file);
		String rank = players.getString("Players." + p.getUniqueId());
		File file2 = new File(plugin.getDataFolder()+File.separator+"data", "ranks.yml");
		FileConfiguration ranks = YamlConfiguration.loadConfiguration(file2);
		
		return ranks.getStringList("Ranks." + rank + ".permissions");
	}
	
	public static String getPlayerRank(Player p) {
		File file = new File(plugin.getDataFolder()+File.separator+"data", "players.yml");
		FileConfiguration players = YamlConfiguration.loadConfiguration(file);
		return players.getString("Players." + p.getUniqueId());
	}
	public static boolean checkRank(UUID uuid, String rank) {
		File file = new File(plugin.getDataFolder()+File.separator+"data", "players.yml");
		FileConfiguration players = YamlConfiguration.loadConfiguration(file);
		if(players.getString("Players." + uuid).equals(rank)) {
			return true;
		}
		return false;
	}

}
