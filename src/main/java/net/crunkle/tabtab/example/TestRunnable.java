package net.crunkle.tabtab.example;

import net.crunkle.tabtab.api.TabTabAPI;
import org.bukkit.Bukkit;

/**
 * This is an example class of how you may implement
 * the API. It shows how to fetch the instance of the
 * object, set the header text and change a tab slot.
 *
 * @author Jared Tiala
 */
public abstract class TestRunnable implements Runnable {
    private final TabTabAPI tabTabAPI;

    {
        this.tabTabAPI = Bukkit.getServicesManager().load(TabTabAPI.class);
    }

    @Override
    public void run() {
        this.tabTabAPI.setHeader("TabTabAPI");

        this.tabTabAPI.setSlot(null, 1, 1, "Hello World");
    }
}
