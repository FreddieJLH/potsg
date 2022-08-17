package org.ortix.sg.task;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.ortix.sg.SGPlugin;

@Getter
@Setter
public class CountdownTask extends BukkitRunnable {

    private int totalTime = 60;
    private int secondsElapsed = 0;
    private boolean active = false;

    @Override
    public void run() {
        if (!this.active) {
            secondsElapsed = 0;
            return;
        }

        this.secondsElapsed += 1;

        final int remainingTime = this.totalTime - this.secondsElapsed;
        switch (remainingTime) {
            case 60:
            case 30:
            case 15:
            case 10:
            case 5:
            case 4:
            case 3:
            case 2:
            case 1:
                Bukkit.broadcastMessage(
                        ChatColor.YELLOW + "The game will begin in " + ChatColor.LIGHT_PURPLE + remainingTime + " " +
                        (remainingTime == 60 ? "minute" : remainingTime == 1 ? "second" : "seconds") + ChatColor.YELLOW + "."
                );
        }

        if (this.secondsElapsed == 60) {
            SGPlugin.getInstance().getServerHandler().getGame().start();
            this.active = false;
            this.cancel();
        }
    }

}
