package net.crunkle.tabtab.api;

import com.comphenix.packetwrapper.WrapperPlayServerScoreboardTeam;
import net.minecraft.server.v1_7_R4.ChatComponentText;
import net.minecraft.server.v1_7_R4.EnumGamemode;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;
import org.spigotmc.ProtocolInjector;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.UUID;

/**
 * The handle for the API which is accessible by other plugins using
 * the Bukkit services framework. This also allows plugins to modify
 * the tab list and design it in a specific way whilst bypassing
 * the default function of the list.
 *
 * @author Jared Tiala
 */
public final class TabTabAPI {
    private static final String PREFIX;
    private static final String SUFFIX;

    private final JavaPlugin plugin;
    private Team resetTeam;
    private String header;
    private String footer;

    static {
        PREFIX = ChatColor.DARK_GRAY.toString();
        SUFFIX = ": " + ChatColor.RESET;
    }

    {
        this.header = "";
        this.footer = "";
    }

    public TabTabAPI(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Adds a new player to the tab team in order to bypass
     * the online checks and also bypass tab order checks.
     *
     * @param player the player to be added
     */
    public void addTeamPlayer(Player player) {
        this.resetTeam.addPlayer(player);
    }

    /**
     * Removes a player from the tab team once the player
     * is no longer using the tab list.
     *
     * @param player the player to be removed
     */
    public void removeTeamPlayer(Player player) {
        this.resetTeam.removePlayer(player);
    }

    /**
     * Resets the default tab team in order to bypass the
     * online checks and also bypass tab order checks.
     */
    public void resetTeam() {
        this.resetTeam = Bukkit.getScoreboardManager()
                .getMainScoreboard().getTeam("tabReset");

        if (this.resetTeam != null) {
            this.resetTeam.unregister();
        }

        this.resetTeam = Bukkit.getScoreboardManager()
                .getMainScoreboard().registerNewTeam("tabReset");

        this.resetTeam.setPrefix(ChatColor.RESET.toString());

        for (Player player : Bukkit.getOnlinePlayers()) {
            this.addTeamPlayer(player);
        }
    }

    /**
     * @return the current tab header text
     */
    public String getHeader() {
        return this.header;
    }

    /**
     * Sets the tab header text to a new value and also
     * updates all players with the new value.
     *
     * @param header the new tab header text
     */
    public void setHeader(String header) {
        this.header = header;

        this.updateTexts();
    }

    /**
     * @return the current tab footer text
     */
    public String getFooter() {
        return this.footer;
    }

    /**
     * Sets the tab footer text to a new value and also
     * updates all players with the new value.
     *
     * @param footer the new tab footer text
     */
    public void setFooter(String footer) {
        this.footer = footer;

        this.updateTexts();
    }

    /**
     * Updates all players with the current tab header
     * and footer text.
     */
    public void updateTexts() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.initializeText(player);
        }
    }

    /**
     * Creates and sends a packet which will update the
     * client's tab header and footer text.
     *
     * @param target the player to be targeted
     */
    public void initializeText(Player target) {
        if (this.header.length() == 0 && this.footer.length() == 0) {
            return;
        }

        ((CraftPlayer) target).getHandle().playerConnection.sendPacket(
                new ProtocolInjector.PacketTabHeader(
                        new ChatComponentText(this.header.length() > 0 ? this.header + "\n" : ""),
                        new ChatComponentText(this.footer.length() > 0 ? "\n" + this.footer : "")
                )
        );
    }

    /**
     * Creates and sends a series of packets which will
     * create player tab info and the teams associated
     * with each slot.
     *
     * @param target the player to be targeted
     */
    public void initializeList(Player target) {
        for (int slot = 11; slot <= 90; slot++) {
            ((CraftPlayer) target).getHandle().playerConnection.sendPacket(
                    this.createPlayerInfo(String.valueOf(slot))
            );

            this.createScoreboardTeam(String.valueOf(slot)).sendPacket(target);
        }
    }

    /**
     * Changes the text displayed inside of a slot at
     * a specific coordinate for a targeted player by
     * sending a team update packet.
     *
     * @param player the player to be targeted
     * @param x      the x coordinate of the slot
     * @param y      the y coordinate of the slot
     * @param text   the text to be displayed
     */
    public void setSlot(Player player, int x, int y, String text) {
        if (x < 1 || x > 4) {
            return;
        } else if (y < 1 || y > 20) {
            return;
        }

        this.createScoreboardUpdate(getSlotName(x, y), text).sendPacket(player);
    }

    /**
     * Fetches the numerical name of the slot based on
     * the x and y coordinates.
     *
     * @param x the x coordinate of the slot
     * @param y the y coordinate of the slot
     * @return the numerical name of the slot
     */
    public String getSlotName(int x, int y) {
        return String.valueOf(11 + (y - 1) % 20 + (x - 1) * 20);
    }

    /**
     * Creates a new player info packet to be sent to
     * a player.
     *
     * @param name the name of the tab slot
     * @return the constructed player info packet
     */
    private PacketPlayOutPlayerInfo createPlayerInfo(String name) {
        PacketPlayOutPlayerInfo playerInfo = new PacketPlayOutPlayerInfo();

        try {
            Field action = playerInfo.getClass().getDeclaredField("action");
            Field username = playerInfo.getClass().getDeclaredField("username");
            Field player = playerInfo.getClass().getDeclaredField("player");
            Field ping = playerInfo.getClass().getDeclaredField("ping");
            Field gamemode = playerInfo.getClass().getDeclaredField("gamemode");

            action.setAccessible(true);
            username.setAccessible(true);
            player.setAccessible(true);
            ping.setAccessible(true);
            gamemode.setAccessible(true);

            action.set(playerInfo, 0);
            username.set(playerInfo, name + TabTabAPI.SUFFIX);
            player.set(playerInfo, new GameProfile(UUID.randomUUID(), name + TabTabAPI.SUFFIX));
            ping.set(playerInfo, 0);
            gamemode.set(playerInfo, EnumGamemode.NONE.getId());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return playerInfo;
    }

    /**
     * Creates a new scoreboard team packet in order to
     * handle the creation of a new team.
     *
     * @param name the name of the team
     * @return the constructed scoreboard team packet
     */
    private WrapperPlayServerScoreboardTeam createScoreboardTeam(String name) {
        WrapperPlayServerScoreboardTeam scoreboardTeam = new WrapperPlayServerScoreboardTeam();

        HashSet<String> players = new HashSet<String>();

        players.add(name + TabTabAPI.SUFFIX);

        scoreboardTeam.setPacketMode((byte) 0);
        scoreboardTeam.setTeamName("t" + name);
        scoreboardTeam.setPlayers(players);
        scoreboardTeam.setTeamPrefix(TabTabAPI.PREFIX);

        return scoreboardTeam;
    }

    /**
     * Creates a new scoreboard team packet in order to
     * handle the options update of a defined team.
     *
     * @param name the name of the team
     * @param text the text to be displayed
     * @return the constructed scoreboard team packet
     */
    private WrapperPlayServerScoreboardTeam createScoreboardUpdate(String name, String text) {
        WrapperPlayServerScoreboardTeam scoreboardTeam = new WrapperPlayServerScoreboardTeam();

        scoreboardTeam.setPacketMode((byte) 2);
        scoreboardTeam.setTeamName("t" + name);
        scoreboardTeam.setTeamPrefix(TabTabAPI.PREFIX);
        scoreboardTeam.setTeamSuffix(text);

        return scoreboardTeam;
    }
}
