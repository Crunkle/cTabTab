package net.crunkle.tabtab.api.listeners;

import net.crunkle.tabtab.api.TabTabAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * This is the core listener to detect any players joining or
 * leaving the server. When a new player connects to the
 * server, it will initialize the default structure defined
 * in the {@link TabTabAPI} class.
 *
 * @author Jared Tiala
 */
public final class TabTabListener implements Listener {
    private final TabTabAPI tabTabAPI;

    {
        this.tabTabAPI = Bukkit.getServicesManager().load(TabTabAPI.class);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.tabTabAPI.addTeamPlayer(event.getPlayer());

        this.tabTabAPI.initializeText(event.getPlayer());
        this.tabTabAPI.initializeList(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.tabTabAPI.removeTeamPlayer(event.getPlayer());
    }
}
