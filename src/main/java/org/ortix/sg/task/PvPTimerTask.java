package org.ortix.sg.task;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public class PvPTimerTask extends BukkitRunnable {

    private int secondsElapsed = 0;
    private boolean active = true;
    private final float totalTime = 120F;

    @Override
    public void run() {
        secondsElapsed += 1;

//        Bukkit.getOnlinePlayers().forEach(player ->
//                FrozenBossBarHandler.setBossBar(
//                        player,
//                        Chat.translate("&f&lPVP Timer: &4&l" + TimeUtils.formatIntoMMSS((int) (totalTime - secondsElapsed))),
//                        (totalTime - this.secondsElapsed) / totalTime)
//        );

        if (secondsElapsed == totalTime) {
            this.active = false;

            Bukkit.getOnlinePlayers().forEach(player -> {
//                FrozenBossBarHandler.removeBossBar(player);
                player.sendMessage(ChatColor.DARK_RED + "PVP is now enabled!");
            });

            this.cancel();
        }
    }

}
