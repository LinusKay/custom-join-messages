package customjoin.main;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinLeave implements org.bukkit.event.Listener
{
  private Main plugin;
  public static String prefix;
  public static String defaultjoin;
  public static String defaultquit;
  
  public JoinLeave(Main pl)
  {
    plugin = pl;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
    prefix = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.plugin-prefix"));
    defaultjoin = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.default-join-message"));
    defaultquit = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.default-quit-message"));
  }
  
  @EventHandler
  public void onJoin(PlayerJoinEvent e) {
    Player p = e.getPlayer();
    e.setJoinMessage(eventMessage("Join", p, defaultjoin));
  }
  
  @EventHandler
  public void onQuit(PlayerQuitEvent e) {
    Player p = e.getPlayer();
    e.setQuitMessage(eventMessage("Quit", p, defaultquit));
  }
  
  public String eventMessage(String joinquit, Player p, String defaultString) {
    if (p.hasPermission("customjoin." + joinquit.toLowerCase())) {
      if (plugin.getConfig().getString(p.getUniqueId() + "." + joinquit) != null) {
        String msg = plugin.getConfig().getString(p.getUniqueId() + "." + joinquit);
        return msg.replace("{PLAYER}", p.getName());
      }
      return defaultString.replace("{PLAYER}", p.getName());
    }
    

    return defaultString.replace("{PLAYER}", p.getName());
  }
}
