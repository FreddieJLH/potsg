package org.ortix.sg.tab;

import net.frozenorb.qlib.tab.LayoutProvider;
import net.frozenorb.qlib.tab.TabLayout;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.ortix.sg.SGPlugin;
import org.ortix.sg.game.Game;

public class SGTabProvider implements LayoutProvider {

    private int currentX = 0;
    private int currentY = 3;

    @Override
    public TabLayout provide(Player player) {
        final TabLayout tabLayout = TabLayout.create(player);
        final SGPlugin plugin = SGPlugin.getInstance();
        final Game game = plugin.getServerHandler().getGame();

        tabLayout.set(0, 1, "&7Players: " + game.getRemainingPlayers().size());
        tabLayout.set(1, 0, "&6&l" + plugin.getServerConfig().getServerName());
        tabLayout.set(2, 1, "&7Spectators: " + game.getSpectators().size());

        game.getRemainingPlayers().forEach(remainingPlayer -> this.updateSlot(tabLayout, remainingPlayer.getDisplayName()));
        game.getSpectators().forEach(spectatingUuid -> this.updateSlot(tabLayout, ChatColor.GRAY + UUIDUtils.name(spectatingUuid)));

        return tabLayout;
    }

    public void updateSlot(TabLayout tabLayout, String content) {
        tabLayout.set(this.currentX, this.currentY, content);

        if (this.currentX != 2) {
            this.currentX += 1;
        } else {
            this.currentX = 0;
            this.currentY += 1;
        }
    }

}
