package probending;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

public class PBTeleporter {
    private PBScoreBoard scoreboard;
    private PBGameStart gamestart;
    private PBWarps warps;
    
    boolean DeathMatch = false;
    private Map<String, Integer> playerLocation = new HashMap<>();
    private Map<String, Integer> playerFaults = new HashMap<>();
    
    //Facing the right way! Need a solution for this, so it gets saved as well!
    double pitch1 = ((90 + 90) * Math.PI) / 180;
    double yaw1 = ((90 + 90) * Math.PI) / 180;
    Vector red = new Vector((Math.sin(pitch1) * Math.cos(yaw1)), (Math.sin(pitch1) * Math.sin(yaw1)), Math.cos(pitch1));
    double pitch2 = ((270 + 90) * Math.PI) / 180;
    double yaw2 = ((90 + 90) * Math.PI) / 180;
    Vector blue = new Vector((Math.sin(pitch2) * Math.cos(yaw2)), (Math.sin(pitch2) * Math.sin(yaw2)), Math.cos(pitch2));

    public PBTeleporter(PBScoreBoard scoreboard, PBGameStart gamestart) {
        this.scoreboard = scoreboard;
        this.gamestart = gamestart;
        warps = new PBWarps(Probending.getInstance());
    }

    //Game start
    public void startGame() {
        playerLocation.clear();
        int bluePlayerNumber = 1;
        int redPlayerNumber = 1;
        for (Team team : scoreboard.board.getTeams()) {
            if ("blue".equals(team.getName())) {
                for (OfflinePlayer p : team.getPlayers()) {
                    switch (bluePlayerNumber){
                        case 1:
                            p.getPlayer().teleport(warps.getSpawn("blue1"));
                            bluePlayerNumber++;
                            break;
                        case 2:
                            p.getPlayer().teleport(warps.getSpawn("blue2"));
                            bluePlayerNumber++;
                            break;
                        case 3:
                            p.getPlayer().teleport(warps.getSpawn("blue3"));
                            bluePlayerNumber++;
                            break;
                    }
                    playerLocation.put(p.getName(), 3);
                    playerFaults.put(p.getName(), 0);
                }
            }
            if ("red".equals(team.getName())) {
                for (OfflinePlayer p : team.getPlayers()) {
                    switch (redPlayerNumber){
                        case 1:
                            p.getPlayer().teleport(warps.getSpawn("red1").setDirection(red));
                            redPlayerNumber++;
                            break;
                        case 2:
                            p.getPlayer().teleport(warps.getSpawn("red2").setDirection(red));
                            redPlayerNumber++;
                            break;
                        case 3:
                            p.getPlayer().teleport(warps.getSpawn("red3").setDirection(red));
                            redPlayerNumber++;
                            break;
                    }
                    playerLocation.put(p.getName(), 4);
                    playerFaults.put(p.getName(), 0);
                }
            }
        }
    }

    //Game in progress!
    public void gamePlaces(Player player, String toPlaceStr, String gameType) {
        String teamName = scoreboard.board.getPlayerTeam(player).getName();
        if(teamName == null){
            return;
        }
        
        int fromPlace = playerLocation.get(player.getName());
        int toPlace = placeStrToInt(toPlaceStr);
        int fDir = 0;
        switch (teamName) {
            case "blue":
                fDir = 1;
                break;
            case "red":
                fDir = -1;
                break;
        }
        
        if (!DeathMatch) {
            if (fromPlace > 0 && fromPlace < 7) {
                if (toPlace > 0 && toPlace < 7) {
                    if (toPlace == fromPlace - fDir) {//Hit line behind!
                        setPlayerLocation(player, toPlace, teamName);
                        scoreboard.broadcast(ChatColor.GOLD + "Player: " + ChatColor.BLUE + player.getDisplayName() + ChatColor.GOLD + " moved 1 place back!");
                    } else if (toPlace == fromPlace + fDir) {//Hit line infront!
                        frontLineHit(player, fromPlace-fDir, teamName, fromPlace);
                    } else if (fromPlace - toPlace == 0) {//Player gets teleported!
                    } else {//This player is hacking ?_?
                    }
                    if((fromPlace - extremeLocation(teamName))*fDir > 0){ //The team can move forward!
                        teleportTeamForward(teamName, fromPlace);
                    }
                } else if (toPlace == 0 || toPlace == 7) {//over rand
                    setPlayerLocation(player, toPlace, teamName);
                }
                if(extremeLocation(teamName) == ((1-fDir)*7)/2){
                    gamestart.endGame(teamName, gameType);
                }
            } else if (fromPlace == 0 || fromPlace == 7) {
                if (toPlace >= 0 || toPlace <= 7) {
                    setPlayerLocation(player, toPlace, teamName);
                }
            }
        } else {
            //DeathMatch!
        }
    }
    
    //Hit line infront of you!
    private void frontLineHit(Player player, int iplace, String team, int fromPlace) {
        playerFaults.put(player.getName(), (playerFaults.get(player.getName()) + 1));
        if (playerFaults.get(player.getName()) < 3){
            if (team.equals("red")) {
                player.teleport(warps.getSpawn(placeIntToStr(fromPlace)).setDirection(red));
            } else if (team.equals("blue")) {
                player.teleport(warps.getSpawn(placeIntToStr(fromPlace)).setDirection(blue));
            }
            scoreboard.broadcast(ChatColor.GOLD + "Player: " + ChatColor.BLUE + player.getDisplayName() + ChatColor.GOLD 
                    + " has now " + ChatColor.DARK_RED + playerFaults.get(player.getName()) + ChatColor.GOLD + " faults!");
        } else {
            scoreboard.broadcast(ChatColor.GOLD + "Player: " + ChatColor.BLUE + player.getDisplayName() + ChatColor.GOLD 
                    + " has now " + ChatColor.DARK_RED + playerFaults.get(player.getName()) + ChatColor.GOLD + " faults and will be teleported 1 place back!");
            setPlayerLocation(player, iplace, team);
            playerFaults.put(player.getName(), 0);
        }
    }
    
    private void setPlayerLocation(Player player, int iplace, String team) {
        String place = placeIntToStr(iplace);
        playerLocation.put(player.getName(), iplace);
        setPlayerLocation(player, place, team);
    }

    private void setPlayerLocation(Player player, String place, String team) {
        if (team.equals("red")) {
            player.teleport(warps.getSpawn(place).setDirection(red));
        } else if (team.equals("blue")) {
            player.teleport(warps.getSpawn(place).setDirection(blue));
        }
    }
    
    private void teleportTeamForward(String teamName, int iplace){
        Team eTeam = null;
        for(Team team : scoreboard.board.getTeams()){
            if(!team.getName().equals(teamName)){
                eTeam = team;
            }
        }
        for(OfflinePlayer offPlayer : eTeam.getPlayers()){
            Player player = offPlayer.getPlayer();
            if (playerLocation.get(player.getName()) == 7 || playerLocation.get(player.getName()) == 0) {
                player.sendMessage(ChatColor.DARK_GRAY + "Your team was moved forward, you were to bad...");
            } else {
                setPlayerLocation(player,iplace,eTeam.getName());
            }
        }
    }
    
    private int extremeLocation(String teamName){
        Team team = scoreboard.board.getTeam(teamName);
        int fDir;
        int extremeLoc;
        if ("blue".equals(teamName)) {
            fDir = 1;
            extremeLoc = 0;
        } else if ("red".equals(teamName)) {
            fDir = -1;
            extremeLoc = 7;
        } else {
            //error
            fDir = 0;
            extremeLoc = 0;
        }
       
        int newLoc;
        for(OfflinePlayer offPlayer : team.getPlayers()){
            newLoc = playerLocation.get(offPlayer.getName());
            if((newLoc - extremeLoc)*fDir > 0){
                extremeLoc = newLoc;
            }
        }
        return extremeLoc;
    }
    
    public String getLosingTeam(){
        int blueloc = extremeLocation("blue");
        int redloc = 7-extremeLocation("red");
        if(redloc < blueloc) {// red wint
            return "red";
        } else if(redloc > blueloc) {// blue wint
            return "blue";
        } else {// gelijk
            return "draw";
        }
    }
    
    private int placeStrToInt(String strPlace) {
        switch (strPlace) {
            case "b4":
                return 0;
            case "b3":
                return 1;
            case "b2":
                return 2;
            case "b1":
                return 3;
            case "r1":
                return 4;
            case "r2":
                return 5;
            case "r3":
                return 6;
            case "r4":
                return 7;
            case "dm":
                return 20;
        }
        return -1;
    }

    private String placeIntToStr(int intPlace) {
        switch (intPlace) {
            case 0:
                return "b4";
            case 1:
                return "b3";
            case 2:
                return "b2";
            case 3:
                return "b1";
            case 4:
                return "r1";
            case 5:
                return "r2";
            case 6:
                return "r3";
            case 7:
                return "r4";
            case 20:
                return "dm";
        }
        return null;
    }

//=========================================================================================================
    public void teleportSpawn(Player player) {
        player.teleport(warps.getSpawn("spawn"));
    }
    
    public void teleportElementChange(Player player){
        player.teleport(warps.getSpawn("change"));
    }
    
    //Need another way for this \/
    public void setSpawn(String name, Location location) {
        warps.setSpawn(name, location);
    }
}
