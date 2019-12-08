package me.tryce.basicranks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.tryce.basicranks.api.Api;

public class CMD implements CommandExecutor{
	
	private Main plugin = Main.getPlugin(Main.class);
	private String prefix = "§8[§4BasicRanks§8] §7";
	private String sep = "§8§m------------------§8 [§4BasicRanks§8] §8§m------------------";
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		plugin.fixRemovedRanks();
		if(sender.isOp() || sender.hasPermission("basicranks.admin")) {
			File localeDir = new File(plugin.getDataFolder(), "data");
		    if (!localeDir.exists()) {
		        localeDir.mkdirs();
		    }
			
			File ranksFile = new File(plugin.getDataFolder()+File.separator+"data", "ranks.yml");
			FileConfiguration ranksConfig = YamlConfiguration.loadConfiguration(ranksFile);
			
			if(!ranksFile.exists() || ranksFile == null) {
				try {
					ranksFile.createNewFile();		
					ranksConfig.save(ranksFile);	
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}	
			
			File playersFile = new File(plugin.getDataFolder()+File.separator+"data", "players.yml");
			FileConfiguration playersConfig = YamlConfiguration.loadConfiguration(playersFile);
			
			if(!playersFile.exists() || playersFile == null) {
				try {
					playersFile.createNewFile();		
					playersConfig.save(playersFile);	
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}	
			
			if(args.length > 0) {
				if(args[0].equalsIgnoreCase("help")) {
					sendHelp(sender);
				}else if((args[0].equalsIgnoreCase("listinh") || args[0].equalsIgnoreCase("listinherits")|| args[0].equalsIgnoreCase("inherits") || args[0].equalsIgnoreCase("inheritance")|| args[0].equalsIgnoreCase("permslist"))&& args.length == 2) {
					if(Api.rankExists(args[1])) {
						ArrayList<String> inherits = (ArrayList<String>) ranksConfig.getStringList("Ranks."+args[1]+".inherits");
						sender.sendMessage(prefix + args[1] +"'s inherits§8: ");
						for(String groups : inherits) {
							sender.sendMessage(prefix + "§c- "+ groups);
						}
					}else {
						sender.sendMessage(prefix + "Rank doesn't exist.");
					}
				}else if((args[0].equalsIgnoreCase("removeinh") ||args[0].equalsIgnoreCase("removeinherit") ||args[0].equalsIgnoreCase("removeinheritance") ||args[0].equalsIgnoreCase("removeinh") ||args[0].equalsIgnoreCase("reminh") ||args[0].equalsIgnoreCase("delinh") || args[0].equalsIgnoreCase("delinherit")|| args[0].equalsIgnoreCase("delinheritance"))&& args.length == 3) {
					if(Api.rankExists(args[1])) {
						if(Api.hasInherit(args[1], args[2])) {
							ArrayList<String> inherits = (ArrayList<String>) ranksConfig.getStringList("Ranks."+args[1]+".inherits");
							inherits.remove(args[2]);
							ranksConfig.set("Ranks."+args[1]+".inherits", inherits);
							try {
								ranksConfig.save(ranksFile);
							} catch (IOException e) {
								e.printStackTrace();
							}
							sender.sendMessage(prefix + "Removed an inheritance from " + args[1] + "§8: §c" + args[2]);
						}else {
							sender.sendMessage(prefix + "Rank doesn't have inherit.");
						}
					}else {
						sender.sendMessage(prefix + "Rank doesn't exist.");
					}
				}else if((args[0].equalsIgnoreCase("addinh") || args[0].equalsIgnoreCase("addinherit")|| args[0].equalsIgnoreCase("addinheritance"))&& args.length == 3) {
					if(Api.rankExists(args[1])) {
						if(!Api.hasInherit(args[1], args[2])) {
							ArrayList<String> inherits = (ArrayList<String>) ranksConfig.getStringList("Ranks."+args[1]+".inherits");
							inherits.add(args[2]);
							ranksConfig.set("Ranks."+args[1]+".inherits", inherits);
							try {
								ranksConfig.save(ranksFile);
							} catch (IOException e) {
								e.printStackTrace();
							}
							sender.sendMessage(prefix + "Added an inheritance to " + args[1] + "§8: §c" + args[2]);
						}else {
							sender.sendMessage(prefix + "Rank already has inherit.");
						}
					}else {
						sender.sendMessage(prefix + "Rank doesn't exist.");
					}
				}else if((args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("reloadplugin")|| args[0].equalsIgnoreCase("rl"))&& args.length == 1) {
					sender.sendMessage(prefix +"Reloading the plugin!");
					plugin.getServer().getPluginManager().disablePlugin(plugin);
					plugin.getServer().getPluginManager().enablePlugin(plugin);
					
				}else if((args[0].equalsIgnoreCase("chatformat") || args[0].equalsIgnoreCase("setformat")|| args[0].equalsIgnoreCase("setchatformat"))&& args.length > 2) {
					if(Api.rankExists(args[1])) {
						StringBuilder builder = new StringBuilder();
						for (int i = 2; i < args.length; i++) {
							builder.append(args[i]).append(" ");
						}
						String format = builder.toString();
						String raw = format.substring(0, format.length()-1);
						format = format.replaceAll("%rank%", args[1]);
						format = format.replaceAll("%player%", "trycedev");
						format = format.replaceAll("%msg%", "What's up people!");
						sender.sendMessage(prefix + "Set chat format for " + args[1] + "§8: §r" + ChatColor.translateAlternateColorCodes('&', format));
						ranksConfig.set("Ranks."+args[1]+".chatFormat", raw);
						try {
							ranksConfig.save(ranksFile);
						} catch (IOException e) {
							e.printStackTrace();
						}	
					}
				}else if((args[0].equalsIgnoreCase("usechat") || args[0].equalsIgnoreCase("setusechat")|| args[0].equalsIgnoreCase("enablechat"))&& args.length == 1) {
					plugin.getConfig().set("use-chat", !plugin.getConfig().getBoolean("use-chat"));
					plugin.saveConfig();
					sender.sendMessage(prefix + "use-chat set to§8: §c" + plugin.getConfig().getBoolean("use-chat"));
				}else if((args[0].equalsIgnoreCase("setdefault") || args[0].equalsIgnoreCase("default")|| args[0].equalsIgnoreCase("defaultset"))&& args.length == 2) {
					if(Api.rankExists(args[1])) {
						if(!Api.isDefault(args[1])) {
							plugin.getConfig().set("default-rank", args[1]);
							plugin.saveConfig();
							sender.sendMessage(prefix + "Set default-rank to§8: §c" + args[1]);
						}else {
							sender.sendMessage(prefix + "Rank is already default-rank.");
						}
					}else {
						sender.sendMessage(prefix + "Rank doesn't exist.");
					}
				}else if((args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("assign")|| args[0].equalsIgnoreCase("setrank") || args[0].equalsIgnoreCase("playerset"))&& args.length == 3) {
					if(Api.rankExists(args[2])) {
						OfflinePlayer op = Bukkit.getOfflinePlayer(args[1]);
						if (op.hasPlayedBefore()) {
						    playersConfig.set("Players." + op.getUniqueId(), args[2]);
						    try {	
								playersConfig.save(playersFile);	
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						    sender.sendMessage(prefix + "Set §c" + op.getName() + "§7's rank to §c" + args[2]);
						} else {
							sender.sendMessage(prefix + "Cannot find player§8: §c" + args[1]);
						}
					}else {
						sender.sendMessage(prefix + "Rank doesn't exist.");
					}
				}else if((args[0].equalsIgnoreCase("listperm") || args[0].equalsIgnoreCase("listperms")|| args[0].equalsIgnoreCase("perms") || args[0].equalsIgnoreCase("permlist")|| args[0].equalsIgnoreCase("permslist"))&& args.length == 2) {
					if(Api.rankExists(args[1])) {
						ArrayList<String> perms = (ArrayList<String>) ranksConfig.getStringList("Ranks."+args[1]+".permissions");
						sender.sendMessage(prefix + args[1] +"'s permissions§8: ");
						for(String perm : perms) {
							sender.sendMessage(prefix + "§c- "+ perm);
						}
					}else {
						sender.sendMessage(prefix + "Rank doesn't exist.");
					}
				}else if((args[0].equalsIgnoreCase("delperm") || args[0].equalsIgnoreCase("remperm")|| args[0].equalsIgnoreCase("permrem") || args[0].equalsIgnoreCase("permdel"))&& args.length == 3) {
					if(Api.rankExists(args[1])) {
						if(Api.hasPermission(args[1], args[2])) {
							ArrayList<String> perms = (ArrayList<String>) ranksConfig.getStringList("Ranks."+args[1]+".permissions");
							perms.remove(args[2]);
							ranksConfig.set("Ranks."+args[1]+".permissions", perms);
							try {
								ranksConfig.save(ranksFile);
							} catch (IOException e) {
								e.printStackTrace();
							}	
							sender.sendMessage(prefix + "Removed permission§8: §c" + args[2]);
						}else {
							sender.sendMessage(prefix + "Rank doesn't have permission§8: §c" + args[2]);
						}
					}else {
						sender.sendMessage(prefix + "Rank doesn't exist.");
					}

				}else if((args[0].equalsIgnoreCase("addperm") || args[0].equalsIgnoreCase("permadd"))&& args.length == 3) {
					if(Api.rankExists(args[1])) {
						ArrayList<String> perms = (ArrayList<String>) ranksConfig.getStringList("Ranks."+args[1]+".permissions");
						ArrayList<String> failed = new ArrayList<>();
						int xum = 0;
						if(args[2].contains(";")) {
							sender.sendMessage(prefix + "Added permissions§8:");
							for(String string : args[2].split(";")) {
								if(perms.contains(string)) {
									failed.add(string);
								}else {
									perms.add(string);
									sender.sendMessage(prefix + "§c- " + string);
									xum++;
								}
							}
							if(xum == 0) {
								sender.sendMessage(prefix + "§cNo permissions...");
							}
							if(failed.size() > 0) {
								sender.sendMessage(prefix + "Failed§8:");
								for(String str : failed) {
									sender.sendMessage(prefix + "§c- " + str);
								}
							}
							
						}else {
							if(perms.contains(args[2])) {
								sender.sendMessage(prefix + "§7Failed§8: §cRank already has \""+ args[2]+"\"");
							}else {
								perms.add(args[2]);
								sender.sendMessage(prefix + "Added permission§8: §c" + args[2]);
							}
						}
						ranksConfig.set("Ranks."+args[1]+".permissions", perms);
						try {
							ranksConfig.save(ranksFile);
						} catch (IOException e) {
							e.printStackTrace();
						}	
					}else {
						sender.sendMessage(prefix + "Rank doesn't exist.");
					}
				}else if((args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("ranks"))&& args.length == 1) {
					String msg = "";
					for(String name : Api.getAllRanks()) {
						msg = msg + name + ", ";
					}
					if(msg.equals("")) {
						msg = "No existing ranks...";
					}
					sender.sendMessage(prefix + "§cAll existing groups§8: §7" + msg.substring(0, msg.length() -2));
				}
				else if((args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("delete"))&& args.length == 2) {
					if(Api.rankExists(args[1])) {
						ranksConfig.set("Ranks."+args[1], null);
						try {
							ranksConfig.save(ranksFile);
						} catch (IOException e) {
							e.printStackTrace();
						}	
						sender.sendMessage(prefix + "Removed the rank: §c" + args[1]);
					}else {
						sender.sendMessage(prefix + "Rank doesn't exist.");
					}
				}
				else if(args[0].equalsIgnoreCase("create") && args.length == 2) {
					if(!Api.rankExists(args[1])) {
						
						
						ranksConfig.set("Ranks."+args[1]+".chatFormat", "&4[%rank%] %player%&8: &7%msg%");
						
						ArrayList<String> perms = new ArrayList<String>();
						ranksConfig.set("Ranks."+args[1]+".permissions", perms);
						
						ArrayList<String> inherit = new ArrayList<String>();
						ranksConfig.set("Ranks."+args[1]+".inherits", inherit);
						
						sender.sendMessage(prefix + "Created a rank named §c" + args[1]);
						try {
							ranksConfig.save(ranksFile);
						} catch (IOException e) {
							e.printStackTrace();
						}	
						
					}else {
						sender.sendMessage(prefix + "Rank already exists!");
					}
				}else {
					sendHelp(sender);
				}
			}else {
				sendHelp(sender);
			}
		}else {
			sender.sendMessage(prefix + "BasicRanks running v" + plugin.getDescription().getVersion());
		}
		return true;
	}
	
	public void sendHelp(CommandSender sender) {
		sender.sendMessage(sep);
		sender.sendMessage("§7/basicranks create <rank> §c§oCreate a new rank, must be unique!");
		sender.sendMessage("§7/basicranks remove <rank> §c§oRemove an existing rank.");
		sender.sendMessage("§7/basicranks list §c§oList all ranks.");
		sender.sendMessage("§7/basicranks addPerm <rank> <permission> §c§oAdd a permission to a certain rank.");
		sender.sendMessage("§7/basicranks delPerm <rank> <permission> §c§oRemove a permission from a certain rank.");
		sender.sendMessage("§7/basicranks addInherit <rank> <inheritance> §c§oAdd an inheritance to a certain rank.");
		sender.sendMessage("§7/basicranks delInherit <rank> <inheritance> §c§oRemove an inheritance from a certain rank.");
		sender.sendMessage("§7/basicranks listPerms <rank> §c§oList all permissions from a rank.");
		sender.sendMessage("§7/basicranks set <player> <rank> §c§oAssign a rank to a player.");
		sender.sendMessage("§7/basicranks chatFormat <rank> <chatformat> §c§oSet a chat format to a rank, use !cancel to cancel process.");
		sender.sendMessage("§7/basicranks setDefault <rank> §c§oSet a rank to the default-rank in config.");
		sender.sendMessage("§7/basicranks enableChat §c§oEnable use-chat in config.");
		sender.sendMessage(sep);
	}
}
