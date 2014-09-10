package net.crunkle.tabtab;

import net.crunkle.tabtab.api.TabTabAPI;
import net.crunkle.tabtab.api.listeners.TabTabListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This is the core plugin used to manage the API, initialize
 * it inside of the Bukkit services manager and disable it
 * once the plugin is disabled.
 *
 * @author Jared Tiala
 */
public class TabTabPlugin extends JavaPlugin {
    private TabTabAPI tabTabAPI;

    @Override
    public void onLoad() {
        this.tabTabAPI = new TabTabAPI(this);

        Bukkit.getServicesManager().register(TabTabAPI.class,
                this.tabTabAPI, this, ServicePriority.Normal);
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new TabTabListener(), this);
    }

    @Override
    public void onDisable() {
        Bukkit.getServicesManager().unregister(TabTabAPI.class, this.tabTabAPI);
    }
}
