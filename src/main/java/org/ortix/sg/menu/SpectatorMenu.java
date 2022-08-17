package org.ortix.sg.menu;

import com.google.common.collect.Maps;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.pagination.PaginatedMenu;
import org.bukkit.entity.Player;
import org.ortix.sg.SGPlugin;

import java.util.Map;

public class SpectatorMenu extends PaginatedMenu {

    public SpectatorMenu() {
        this.setAutoUpdate(true);
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Players Remaining";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttonMap = Maps.newHashMap();

        SGPlugin.getInstance().getServerHandler().getGame().getRemainingPlayers().forEach(remainingPlayer ->
                buttonMap.put(buttonMap.size(), new SpectatePlayerButton(remainingPlayer)));

        return buttonMap;
    }

    @Override
    public int getMaxItemsPerPage(Player player) {
        return 18;
    }
}
