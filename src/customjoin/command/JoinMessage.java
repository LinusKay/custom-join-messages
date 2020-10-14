package customjoin.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import customjoin.main.Main;

public class JoinMessage implements org.bukkit.command.CommandExecutor
{
  String prefix, buffer, missingperm, viewmessage, emptymessage, shortmessage, longmessage, bannedmessage, setmessage, resetmessage, missingarg, usetemplate, jointemplate, quittemplate;

  
  public JoinMessage(customjoin.main.Main pl)
  {
    plugin = pl;
    prefix = org.bukkit.ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.plugin-prefix"));
    minlength = Integer.parseInt(plugin.getConfig().getString("options.minimum-length"));
    maxlength = Integer.parseInt(plugin.getConfig().getString("options.maximum-length"));
    missingperm = org.bukkit.ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.missing-permission"));
    viewmessage = org.bukkit.ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.view-message"));
    emptymessage = org.bukkit.ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.error-empty-message"));
    shortmessage = org.bukkit.ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.error-too-short"));
    longmessage = org.bukkit.ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.error-too-long"));
    bannedmessage = org.bukkit.ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.error-banned-word"));
    setmessage = org.bukkit.ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.message-set"));
    resetmessage = org.bukkit.ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.message-reset"));
    missingarg = org.bukkit.ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.missing-arguments"));
    usetemplate = plugin.getConfig().getString("options.use-template");
    jointemplate = org.bukkit.ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("options.join-template"));
    quittemplate = org.bukkit.ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("options.quit-template")); }

  
  public boolean ruleCheck(Player p, String s, String[] args) { if (args.length < 3) {
      p.sendMessage(prefix + emptymessage);
      return false;
    }
    if (s.length() < minlength) {
      p.sendMessage(prefix + shortmessage.replace("{LENGTH}", new StringBuilder(String.valueOf(minlength)).toString()));
      return false;
    }
    if (s.length() > maxlength) {
      p.sendMessage(prefix + longmessage.replace("{LENGTH}", new StringBuilder(String.valueOf(maxlength)).toString()));
      return false;
    }
    java.util.List<String> blacklist = plugin.getConfig().getStringList("options.banned-phrases");
    for (String str : blacklist) {
      if (s.contains(str)) {
        p.sendMessage(prefix + bannedmessage);
        return false;
      }
    }
    return true; }

  
  public String argJoin(String[] args) { 
	  StringBuilder buffer = new StringBuilder();
    for (int i = 2; i < args.length; i++)
    {
      buffer.append(' ').append(args[i]);
    }
    return buffer.toString(); }

  
  public void writeConfig(String joinquit, Player p, String buffer) { plugin.getConfig().set(p.getUniqueId() + "." + joinquit, null);
    plugin.getConfig().set(p.getUniqueId() + ".username", p.getName());
    plugin.getConfig().set(p.getUniqueId() + "." + joinquit, buffer);
    plugin.saveConfig();
    p.sendMessage(prefix + setmessage.replace("{JOIN/QUIT}", joinquit.toLowerCase()).replace("{MESSAGE}", org.bukkit.ChatColor.translateAlternateColorCodes('&', buffer.toString())).replace("{PLAYER}", p.getName())); }
  
  int minlength;
  int maxlength;
  private Main plugin;
  public void noPerm(Player p, String perm) { p.sendMessage(missingperm.replace("{PERM}", "customjoin." + perm)); }
  

  public void viewMsg(Player p, String joinquit, String defaultString)
  {
    if (plugin.getConfig().getString(p.getUniqueId() + "." + joinquit) != null) {
      p.sendMessage(prefix + viewmessage.replace("{JOIN/QUIT}", joinquit.toLowerCase()).replace("{MESSAGE}", org.bukkit.ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(new StringBuilder().append(p.getUniqueId()).append(".").append(joinquit).toString()))).replace("{PLAYER}", p.getName()));
    } else {
      p.sendMessage(prefix + viewmessage.replace("{JOIN/QUIT}", joinquit.toLowerCase()).replace("{MESSAGE}", defaultString).replace("{PLAYER}", p.getName()));
    }
  }
  
  public void resetMsg(Player p, String joinquit)
  {
    plugin.getConfig().set(p.getUniqueId() + "." + joinquit, null);
    plugin.saveConfig();
    p.sendMessage(prefix + resetmessage.replace("{JOIN/QUIT}", joinquit.toLowerCase()));
  }
  
  public String stripColours(String str) {
    String[] colours = { "a", "b", "c", "d", "e", "f", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "k", "o", "l" };
    for (int i = 0; i < colours.length; i++) {
      str = str.replace("&" + colours[i], "");
    }
    return str;
  }
  
  public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args) {
	Player p = (Player)sender;
	try {
		if (args[0].equalsIgnoreCase("set")) {
			if (args[1].equalsIgnoreCase("join")) {
				if (p.hasPermission("customjoin.set.join")) {
				
				    buffer = argJoin(args).trim();
				    
					if (!ruleCheck(p, buffer, args)) {
					      return false;
					}
				    
					if ((usetemplate.equalsIgnoreCase("true")) || (usetemplate.equalsIgnoreCase("yes"))) {
					  buffer = stripColours(buffer);
					  String output = jointemplate.replace("{USERNAME}", p.getName()).replace("{MESSAGE}", buffer);
					  writeConfig("Join", p, output);
					  return true;
					}
				
					writeConfig("Join", p, buffer);
				
				  } else {
				    noPerm(p, "set.join");
				    return false;
				  }
			
			} else if ((args[1].equalsIgnoreCase("leave")) || (args[1].equalsIgnoreCase("quit"))) {
				if (p.hasPermission("customjoin.set.quit")) {
					buffer = argJoin(args).trim();
					if (!ruleCheck(p, buffer, args)) {
						return false;
					}
					
					if ((usetemplate.equalsIgnoreCase("true")) || (usetemplate.equalsIgnoreCase("yes"))) {
						buffer = stripColours(buffer);
						String output = quittemplate.replace("{PLAYER}", p.getName()).replace("{MESSAGE}", buffer);
						writeConfig("Quit", p, output);
						return true;
					}
					writeConfig("Quit", p, buffer);
					
				  } else {
					  noPerm(p, "set.quit");
					  return false;
				  }
			    }
		}
			  
			
			  if (args[0].equalsIgnoreCase("view")) {
			if (args[1].equalsIgnoreCase("join")) {
			  if (p.hasPermission("customjoin.view.join")) {
			viewMsg(p, "Join", customjoin.main.JoinLeave.defaultjoin);
			  } else {
			    noPerm(p, "view.join");
			    return false;
			  }
			}
			else if ((args[1].equalsIgnoreCase("leave")) || (args[1].equalsIgnoreCase("quit"))) {
			  if (p.hasPermission("customjoin.view.quit")) {
			viewMsg(p, "Quit", customjoin.main.JoinLeave.defaultquit);
			  } else {
			    noPerm(p, "view.quit");
			        return false;
			      }
			    }
			  }
			  
			
			  if (args[0].equalsIgnoreCase("reset")) {
			if (args[1].equalsIgnoreCase("join")) {
			  if (p.hasPermission("customjoin.reset.join")) {
			resetMsg(p, "Join");
			    return true;
			  }
			  noPerm(p, "reset.join");
			  return false;
			}
			
			if ((args[1].equalsIgnoreCase("leave")) || (args[1].equalsIgnoreCase("quit"))) {
			  if (p.hasPermission("customjoin.reset.quit")) {
			resetMsg(p, "Quit");
			    return true;
			  }
			  noPerm(p, "reset.quit");
			      return false;
			    }
			  }
	}
	catch (ArrayIndexOutOfBoundsException e) {
	  p.sendMessage(prefix + missingarg);
	  return false;
	}
	return false;
  }
}
