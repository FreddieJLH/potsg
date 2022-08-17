package org.ortix.sg;

import lombok.Getter;
import lombok.SneakyThrows;
import net.frozenorb.qlib.command.FrozenCommandHandler;
import net.frozenorb.qlib.config.ConfigFactory;
import net.frozenorb.qlib.deathmessage.FrozenDeathMessageHandler;
import net.frozenorb.qlib.scoreboard.FrozenScoreboardHandler;
import net.frozenorb.qlib.tab.FrozenTabHandler;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;
import org.ortix.sg.board.SGBoardConfiguration;
import org.ortix.sg.config.*;
import org.ortix.sg.database.SGDatabase;
import org.ortix.sg.game.GameState;
import org.ortix.sg.handler.SiteHandler;
import org.ortix.sg.listener.*;
import org.ortix.sg.pearl.EnderpearlCooldownHandler;
import org.ortix.sg.handler.ServerHandler;
import org.ortix.sg.tab.SGTabProvider;
import org.ortix.sg.task.redis.DataPushThread;

import java.io.*;
import java.security.SecureRandom;

@Getter
public class SGPlugin extends JavaPlugin {

    public static String WORLD_NAME = "current_world";
    public static SecureRandom RANDOM = new SecureRandom();

    private ConfigFactory configFactory;
    private SGDatabase database;
    private SGConfig serverConfig;

    private SiteHandler siteHandler;
    private ServerHandler serverHandler;
    private DataPushThread dataPushThread;

    @Override
    public void onEnable() {
        this.loadWorld();

        this.registerHandlers();
        this.registerListeners();

        this.serverHandler.setGameState(GameState.WAITING);
    }

    @SneakyThrows
    @Override
    public void onDisable() {
        this.configFactory.save("config", this.serverConfig);
        this.dataPushThread.stop();
    }

    private void registerHandlers() {
        this.configFactory = ConfigFactory.newFactory(this);
        this.serverConfig = this.configFactory.fromFile("config", SGConfig.class);

        this.siteHandler = new SiteHandler(this);
        this.serverHandler = new ServerHandler();
        (this.dataPushThread = new DataPushThread()).start();

        if (this.serverConfig.isDatabaseEnabled()) {
            this.database = new SGDatabase(
                    this.serverConfig.getMongoHost(),
                    this.serverConfig.getMongoPort(),
                    this.serverConfig.getMongoDatabase(),
                    this.serverConfig.getMongoUsername().isEmpty() ? null : this.serverConfig.getMongoUsername(),
                    this.serverConfig.getMongoPassword().isEmpty() ? null : this.serverConfig.getMongoPassword());
        }

        FrozenTabHandler.setLayoutProvider(new SGTabProvider());
        FrozenCommandHandler.registerAll(this);
        FrozenScoreboardHandler.setConfiguration(SGBoardConfiguration.create(this));
        FrozenDeathMessageHandler.setConfiguration(new SGDeathMessageConfiguration());
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        Bukkit.getPluginManager().registerEvents(new SpectatorListener(), this);
        Bukkit.getPluginManager().registerEvents(new PreventionListeners(this), this);
        Bukkit.getPluginManager().registerEvents(new BorderListener(this), this);
        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);

        new EnderpearlCooldownHandler(this).init();
    }

    private void loadWorld() {
        File srcDir = new File(this.getServer().getWorldContainer().getPath(), "backup_world");
        File destDir = new File(this.getServer().getWorldContainer().getPath(), WORLD_NAME);

        if (!srcDir.exists()) {
            this.getLogger().severe("World with name backup_world does not exist..");
            this.getServer().shutdown();
            return;
        }

        if (!srcDir.equals(destDir)) {
            try {
                if (destDir.exists())
                    FileUtils.deleteDirectory(destDir);

                if (!destDir.mkdirs()) {
                    getLogger().warning("Couldn't make destination directory.. continuing anyways");
                }

                FileUtils.copyDirectory(srcDir, destDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.getServer().createWorld(new WorldCreator(WORLD_NAME));
        this.getServer().getWorld(WORLD_NAME).setTime(16000);
    }

    public static SGPlugin getInstance() {
        return JavaPlugin.getPlugin(SGPlugin.class);
    }

}
