package probending;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

public class PBGameStart {
    private JavaPlugin plugin;
    private PBScoreBoard scoreboard;
    private PBTeleporter teleporter;
    
    private PBTimer timer;
    private final static int PreGametime = 8;
    private final static int ProbendingMatch1v1 = 90;
    private final static int ProbendingMatch3v3 = 180;
    private final static int DeathMatch = 30;
    private boolean mayWalk = true;
    private boolean preGame = false;
    private boolean hasEnded = true;
    private String gameType;

    public PBGameStart(JavaPlugin plugin, PBScoreBoard scoreboard) {
        this.plugin = plugin;
        this.scoreboard = scoreboard;
    }
    
    public void setTeleporter(PBTeleporter teleporter){
        this.teleporter = teleporter;
    }

    public void startPreGame(final String gameType) {
        teleporter.startGame();
        timer = new PBTimer(this.plugin, this.scoreboard, this) {
            @Override
            public void execute() {
                timerEnded(gameType);
            }
        };
        timer.start(PreGametime, 20L, true);
        mayWalk = false;
        preGame = true;
        hasEnded = false;
        scoreboard.broadcast(ChatColor.DARK_GRAY + "gameType: " + ChatColor.GRAY + gameType);
        for (Team team : scoreboard.board.getTeams()) {
            for (OfflinePlayer offPlayer : team.getPlayers()) {
                Player player = offPlayer.getPlayer();
                player.getInventory().clear();
                //Clear players gear!
                switch (team.getName()) {
                    case "red":
                        gearUp(player, Material.LEATHER_BOOTS, 255, 0, 0);
                        gearUp(player, Material.LEATHER_CHESTPLATE, 255, 0, 0);
                        gearUp(player, Material.LEATHER_LEGGINGS, 255, 0, 0);
                        gearUp(player, Material.LEATHER_HELMET, 255, 0, 0);
                        break;
                    case "blue":
                        gearUp(player, Material.LEATHER_BOOTS, 0, 0, 255);
                        gearUp(player, Material.LEATHER_CHESTPLATE, 0, 0, 255);
                        gearUp(player, Material.LEATHER_LEGGINGS, 0, 0, 255);
                        gearUp(player, Material.LEATHER_HELMET, 0, 0, 255);
                        break;
                }
            }
        }
    }
    
    private void timerEnded(String gameType) {
        if (preGame) {
            startGame(gameType);
        } else {
            endGame(teleporter.getLosingTeam(), gameType);
        }
    }

    private void startGame(String gameType) {
        scoreboard.broadcast(ChatColor.GREEN + "GAME STARTED!");
        switch (gameType) {
            case "1v1":
                timer.setTime(ProbendingMatch1v1, false);
                break;
            case "3v3":
                timer.setTime(ProbendingMatch3v3, false);
                break;
        }
        mayWalk = true;
        preGame = false;
        this.gameType = gameType;
    }

    public void endGame(String teamLossName, String gameType) {
        scoreboard.broadcast(ChatColor.DARK_RED + "GAME ENDED");
        timer.stop();
        
        switch (teamLossName) {
            case "red":
                scoreboard.broadcast(ChatColor.YELLOW + "BLUE WON! players left in it:");
                for (OfflinePlayer offplayer : scoreboard.blue.getPlayers()){
                    Player player = offplayer.getPlayer();
                    if ("1v1".equals(gameType))
                        Methods.addWin1(player);
                    else if ("3v3".equals(gameType))
                        Methods.addWin3(player);
                    scoreboard.broadcast(ChatColor.BLUE + player.getName());
                }
                break;
            case "blue":
                scoreboard.broadcast(ChatColor.YELLOW + "RED WON! players left in it:");
                for (OfflinePlayer offplayer : scoreboard.red.getPlayers()){
                    Player player = offplayer.getPlayer();
                    if ("1v1".equals(gameType))
                        Methods.addWin1(player);
                    else if ("3v3".equals(gameType))
                        Methods.addWin3(player);
                    scoreboard.broadcast(ChatColor.RED + player.getName());
                }
                break;
        }
        scoreboard.broadcast(ChatColor.BLACK + "================================");
        
        for (Team team : scoreboard.board.getTeams()) {
            for (OfflinePlayer offPlayer : team.getPlayers()) {
                Player player = offPlayer.getPlayer();
                scoreboard.objectiveWaiting.getScore(player).setScore(0);
                teleporter.teleportSpawn(player);
                player.getEquipment().getBoots().setType(Material.AIR);
                player.getEquipment().getHelmet().setType(Material.AIR);
                player.getEquipment().getLeggings().setType(Material.AIR);
                player.getEquipment().getChestplate().setType(Material.AIR);
            }
        }
        scoreboard.removePlayersFromTeam();
        hasEnded = true;
        gameType = null;
        scoreboard.updatePlayerScoreboard();
        tryStartGame();
    }
    
     public void outOfGame(Player player, String gameType) {
        scoreboard.objectiveWaiting.getScore(player).setScore(0);
        if (scoreboard.board.getPlayerTeam(player) != null) {
            teleporter.teleportSpawn(player);
            String LosingTeam = scoreboard.board.getPlayerTeam(player).getName();
            scoreboard.board.getPlayerTeam(player).removePlayer(player);
            int teamRedSize = scoreboard.board.getTeam("red").getSize();
            int teamBlueSize = scoreboard.board.getTeam("blue").getSize();
            if (teamBlueSize == 0 || teamRedSize == 0) {
                endGame(LosingTeam, gameType);
            }
        }
    }

    public void MayWalk(Player player) {
        if (!mayWalk && !hasEnded) {
            if (scoreboard.board.getPlayerTeam(player) != null) {
                Location location = player.getLocation();
                player.teleport(location);
            }
        }
    }

    public boolean hasEnded() {
        return hasEnded;
    }
    
    public boolean preGame() {
        return preGame;
    }
        
    public String gameType(){
        return gameType;
    }

//GAMESTART ====================================================================
    public void tryJoinGame(Player player, String gameType) {
        player.sendMessage(ChatColor.DARK_RED + "oOo " + ChatColor.RED + "Probending");
        switch (gameType) {
            case "1v1":
                scoreboard.objectiveWaiting.getScore(player).setScore(1);
                player.sendMessage(ChatColor.DARK_AQUA + "=" + ChatColor.AQUA + "=" + ChatColor.DARK_AQUA + "= " + ChatColor.BLUE + "You have been queued for 1v1's!");
                break;
            case "3v3":
                scoreboard.objectiveWaiting.getScore(player).setScore(2);
                player.sendMessage(ChatColor.DARK_AQUA + "=" + ChatColor.AQUA + "=" + ChatColor.DARK_AQUA + "= " + ChatColor.BLUE + "You have been queued for 3v3's!");
                break;
            default:
                scoreboard.objectiveWaiting.getScore(player).setScore(3);
                player.sendMessage(ChatColor.DARK_AQUA + "=" + ChatColor.AQUA + "=" + ChatColor.DARK_AQUA + "= " + ChatColor.BLUE + "You have been queued for anything that comes first!");
                break;
        }
        tryStartGame();
    }

    public void tryLeaveQueue(Player player) {
        player.sendMessage(ChatColor.DARK_RED + "oOo " + ChatColor.RED + "Probending");
        player.sendMessage(ChatColor.DARK_AQUA + "=" + ChatColor.AQUA + "=" + ChatColor.DARK_AQUA + "= " + ChatColor.RED + "You have been removed from queue/game!");
        outOfGame(player, gameType);   
    }

    private boolean tryStartGame() {
            String gameType;
            Player[] plist;
            
            boolean canStart = false;
            int PlayersQueued1v1 = 0;
            int PlayersQueued3v3 = 0;
            int playersQueued = 0;
            List<Player> queuedPlayers1v1 = new ArrayList<>();
            List<Player> queuedPlayers3v3 = new ArrayList<>();
            
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                if (scoreboard.objectiveWaiting.getScore(p).getScore() == 1 || scoreboard.objectiveWaiting.getScore(p).getScore() == 3) {
                    queuedPlayers1v1.add(p);
                    PlayersQueued1v1++;
                }
            }
            
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                if (scoreboard.objectiveWaiting.getScore(p).getScore() == 2 || scoreboard.objectiveWaiting.getScore(p).getScore() == 3) {
                    queuedPlayers3v3.add(p);
                    PlayersQueued3v3++;
                }
            }
            
            for (Player p : Bukkit.getServer().getOnlinePlayers()){
                if (scoreboard.objectiveWaiting.getScore(p).getScore() == 1 || scoreboard.objectiveWaiting.getScore(p).getScore() == 2 || scoreboard.objectiveWaiting.getScore(p).getScore() == 3) {
                    playersQueued++;
                }
            }
            
            for (Player p : Bukkit.getServer().getOnlinePlayers()){
                if (scoreboard.objectiveWaiting.getScore(p).getScore() == 1 || scoreboard.objectiveWaiting.getScore(p).getScore() == 2 || scoreboard.objectiveWaiting.getScore(p).getScore() == 3) {
                    p.sendMessage(ChatColor.DARK_GRAY + "Player queued: " + ChatColor.GRAY + playersQueued);
                    p.sendMessage(ChatColor.DARK_GRAY + "Player queued for 1v1: " + ChatColor.GRAY + PlayersQueued1v1);
                    p.sendMessage(ChatColor.DARK_GRAY + "Player queued for 3v3: " + ChatColor.GRAY + PlayersQueued3v3);
                }
            }
            
            if (playersQueued >= 2 && hasEnded()) {
                canStart = true;
                if(PlayersQueued3v3 < 6 && PlayersQueued1v1 > 1){
                    gameType = "1v1";
                    int[] ilist = randomList(2, PlayersQueued1v1);
                    plist = new Player[2];
                    for (int i = 0; i < 2; i++)
                        plist[i] = queuedPlayers1v1.get(ilist[i]);
                } else if (PlayersQueued3v3 >=6 ) {
                    gameType = "3v3";
                    int[] ilist = randomList(6,PlayersQueued3v3);
                    plist = new Player[6];
                    for(int i = 0; i < PlayersQueued3v3; i++){
                        plist[i] = queuedPlayers3v3.get(ilist[i]);
                    }
                } else {
                    return false;
                }
                
                if (canStart) {
                    for (int i = 0; i < plist.length / 2; i++) {
                        scoreboard.board.getTeam("red").addPlayer(plist[i]);
                        scoreboard.objectiveWaiting.getScore(plist[i]).setScore(4);
                    }
                    for (int i = (plist.length / 2); i < plist.length; i++) {
                        scoreboard.board.getTeam("blue").addPlayer(plist[i]);
                        scoreboard.objectiveWaiting.getScore(plist[i]).setScore(4);
                    }
                    for (Team team : scoreboard.board.getTeams()) {
                        Bukkit.getServer().broadcastMessage(ChatColor.GREEN + team.getDisplayName() + ":");
                        for (OfflinePlayer p : team.getPlayers()) {
                            Bukkit.getServer().broadcastMessage(ChatColor.GOLD + p.getName());
                        }
                    }
                    Bukkit.getServer().broadcastMessage(ChatColor.DARK_GREEN + "GAME STARTED!");
                    startPreGame(gameType);
                    return true;
                }
        }
        return false;
    }

    private int[] randomList(int amount, int max) {
        Random rand = new Random();
        int[] result = new int[amount];
        outer:
        for (int i = 0; i < amount; i++) {
            int tempResult = rand.nextInt(max);
            for (int j = 0; j < i; j++) {
                if (result[j] == tempResult) {
                    i--;
                    continue outer;
                }
            }
            result[i] = tempResult;
        }
        return result;
    }

//==============================================================================
    private void gearUp(Player player, Material material, Color color) {
        if (material.equals(Material.LEATHER_HELMET)) {
            player.getEquipment().setHelmet(getGear(material, color));
        }
        if (material.equals(Material.LEATHER_CHESTPLATE)) {
            player.getEquipment().setChestplate(getGear(material, color));
        }
        if (material.equals(Material.LEATHER_LEGGINGS)) {
            player.getEquipment().setLeggings(getGear(material, color));
        }
        if (material.equals(Material.LEATHER_BOOTS)) {
            player.getEquipment().setBoots(getGear(material, color));
        }
    }

    private void gearUp(Player player, Material material, int red, int green, int blue) {
        gearUp(player, material, Color.fromBGR(blue, green, red));
    }

    private ItemStack getGear(Material material, Color color) {
        ItemStack item;
        LeatherArmorMeta meta;
        if (!(material.equals(Material.LEATHER_BOOTS) || material.equals(Material.LEATHER_LEGGINGS) || material.equals(Material.LEATHER_CHESTPLATE) || material.equals(Material.LEATHER_HELMET))) {
            return null; //error
        }
        item = new ItemStack(material);
        meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(color);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack getGear(Material material, int red, int green, int blue) {
        return getGear(material, Color.fromBGR(blue, green, red));
    }
}
