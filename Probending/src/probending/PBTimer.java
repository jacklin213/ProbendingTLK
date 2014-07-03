package probending;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class PBTimer {
    private JavaPlugin plugin;
    private PBScoreBoard scoreboard;
    private BukkitRunnable updater;
    private int time;
    boolean showTimes = true;
    boolean PreGame = false;

    public PBTimer(JavaPlugin plugin, PBScoreBoard scoreboard, PBGameStart gamestart) {
        this.plugin = plugin;
        this.scoreboard = scoreboard;
        updater = new BukkitRunnable() {
            @Override
            public void run() {
                updateTime();
            }
        };
    }
 
    public PBTimer(JavaPlugin plugin, PBScoreBoard scoreboard, PBGameStart gamestart, boolean showTimes) {
        this(plugin, scoreboard, gamestart);
        this.showTimes = showTimes;
    }

    public void start(int startTime, long interval, boolean Pregame) {
        this.PreGame = Pregame;
        updater.runTaskTimer(plugin, 0L, interval);
        setTime(startTime, PreGame);
    }

    public void stop() {
        updater.cancel();
        for (Player player : Bukkit.getServer().getOnlinePlayers())
            player.getScoreboard().resetScores(Bukkit.getOfflinePlayer(ChatColor.YELLOW + "Time: " + ChatColor.AQUA + (time)));
    }

    private void updateTime() {
        if (time == 1) {
            for (Player player : Bukkit.getServer().getOnlinePlayers())
                player.getScoreboard().resetScores(Bukkit.getOfflinePlayer(ChatColor.YELLOW + "Time: " + ChatColor.AQUA + (time)));
            execute();
        } else {
            setTime(time - 1, PreGame);
        }
    }

    public void setTime(int time, boolean Pregame) {
        this.PreGame = Pregame;
        this.time = time;
        if (showTimes) {
            clearTime();
        }
        if (PreGame) {
            if (this.time == 3 || this.time == 2 || this.time == 1) {
                scoreboard.broadcast(ChatColor.GOLD + "Game starts in: " + ChatColor.DARK_RED + this.time );
            }
        }
        if (!PreGame) {
            if ((this.time%9) == 0) {
                
            }
        }
    }

    private void clearTime() {
        if (showTimes) {
            for (Player player : Bukkit.getServer().getOnlinePlayers()){
                player.getScoreboard().resetScores(Bukkit.getOfflinePlayer(ChatColor.YELLOW + "Time: " + ChatColor.AQUA + (time + 1)));
                Scoreboard s = player.getScoreboard();
                Objective objectiveSidebar = s.getObjective(DisplaySlot.SIDEBAR);
                objectiveSidebar.getScore(Bukkit.getOfflinePlayer(ChatColor.YELLOW + "Time: " + ChatColor.AQUA + time)).setScore(1);
                player.getScoreboard().resetScores(Bukkit.getOfflinePlayer(ChatColor.YELLOW + "Game: " + ChatColor.DARK_RED + "none"));
            }
        }
    }

    public void execute() {
        //Override this in own version! Done in PBGameStart!
    }
}