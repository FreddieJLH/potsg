package org.ortix.sg.nametag;

import net.frozenorb.qlib.nametag.NametagInfo;
import net.frozenorb.qlib.nametag.NametagProvider;
import org.bukkit.entity.Player;

public class SGNametagProvider extends NametagProvider {

    public SGNametagProvider() {
        super("SG Provider", 1000);
    }

    @Override
    public NametagInfo fetchNametag(Player player, Player viewer) {
        return player == viewer ? createNametag("&a", "") : createNametag("&c", "");
    }

}
