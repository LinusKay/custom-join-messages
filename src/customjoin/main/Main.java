package customjoin.main;

import customjoin.command.JoinMessage;

public class Main extends org.bukkit.plugin.java.JavaPlugin implements org.bukkit.event.Listener {
  public Main() {}
  
  public void onEnable() {
    registerCommands();
    registerConfig();
    registerEvents();
  }
  
  public void registerCommands() {
    getCommand("customjoin").setExecutor(new JoinMessage(this));
  }
  
  public void registerConfig() {
    getConfig().options().copyDefaults(true);
    saveConfig();
  }
  
  public void registerEvents() {
    new JoinLeave(this);
  }
}
