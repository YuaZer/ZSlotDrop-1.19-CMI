package io.github.yuazer.zslotdrop;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import io.github.yuazer.zslotdrop.Commands.MainCommand;
import io.github.yuazer.zslotdrop.Listener.PlayerEvent;
import io.github.yuazer.zslotdrop.Packet.ChestEntityGenerator;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class Main extends JavaPlugin {
    private static Main instance;

    public static Main getInstance() {
        return instance;
    }

    public static String pluginName;

    private static ScriptEngineManager mgr = new ScriptEngineManager();
    private static ScriptEngine engine = mgr.getEngineByName("JavaScript");
    private static ProtocolManager protocolManager;

    public static ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    public void onLoad() {
        protocolManager = ProtocolLibrary.getProtocolManager();
    }

    public static ScriptEngine getEngine() {
        return engine;
    }

    @Override
    public void onEnable() {
        instance = this;
        pluginName = this.getDescription().getName();
        if (Bukkit.getPluginManager().getPlugin("CMI") == null || Bukkit.getPluginManager().getPlugin("ProtocolLib")==null) {
            getLogger().info("§cCMI或ProtocolLib未加载!插件卸载!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        onLoad();
        ChestEntityGenerator.registerChestOpenEvent();
        logLoaded(this);
        saveDefaultConfig();
        Bukkit.getPluginCommand("zslotdrop").setExecutor(new MainCommand());
        Bukkit.getPluginManager().registerEvents(new PlayerEvent(), this);
    }

    @Override
    public void onDisable() {
        logDisable(this);
    }

    public static void logLoaded(JavaPlugin plugin) {
        Bukkit.getLogger().info(String.format("§e[§b%s§e] §f已加载", plugin.getName()));
        Bukkit.getLogger().info("§b作者:§eZ菌");
        Bukkit.getLogger().info("§b版本:§e" + plugin.getDescription().getVersion());
    }

    public static void logDisable(JavaPlugin plugin) {
        Bukkit.getLogger().info(String.format("§e[§b%s§e] §c已卸载", plugin.getName()));
    }
}
