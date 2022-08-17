package org.ortix.sg.task.redis;

import net.frozenorb.qlib.qLib;
import org.bukkit.scheduler.BukkitRunnable;
import org.ortix.sg.SGPlugin;
import org.ortix.sg.handler.ServerHandler;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

public class DataPushThread extends BukkitRunnable {

    public void start() {
        this.runTaskTimerAsynchronously(SGPlugin.getInstance(), 0L, 20L);
    }

    public void stop() {
        this.cancel();
        try (Jedis jedis = qLib.getInstance().getBackboneJedisPool().getResource()) {
            final ServerHandler serverHandler = SGPlugin.getInstance().getServerHandler();

            jedis.del("sg-game-" + serverHandler.getGame().getMatchUuid());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void run() {
        try (Jedis jedis = qLib.getInstance().getBackboneJedisPool().getResource()) {
            final Map<String, String> hash = new HashMap<>();
            final SGPlugin plugin = SGPlugin.getInstance();
            final ServerHandler serverHandler = plugin.getServerHandler();

            hash.put("spectatorCount", serverHandler.getGame().getSpectators().size() + "");
            hash.put("remainingPlayerCount", serverHandler.getGame().getRemainingPlayers().size() + "");
            hash.put("startedAt", serverHandler.getGame().getStartTime() + "");
            hash.put("borderSize", serverHandler.getGame().getBorder().getSize() + "");
            hash.put("state", serverHandler.getGameState().name());

            jedis.hset("sg-game-" + serverHandler.getGame().getMatchUuid() + ":", hash);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
