package net.crunkle.tabtab.api.adapters;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.minecraft.server.v1_7_R4.EnumGamemode;
import org.bukkit.plugin.Plugin;

/**
 * This prevents regular player info packets from being sent
 * to players in order to make room for the improved tab
 * list. It will check specific portions of the packet in
 * order to determine which packets are valid.
 *
 * @author Jared Tiala
 */
public final class TabPacketAdapter extends PacketAdapter {
    public TabPacketAdapter(Plugin plugin) {
        super(plugin, PacketType.Play.Server.PLAYER_INFO);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        // Check if the player's gamemode is set to an unreachable one.

        if (event.getPacket().getIntegers().read(1) != EnumGamemode.NONE.getId()) {
            event.setCancelled(true);
        }
    }
}
