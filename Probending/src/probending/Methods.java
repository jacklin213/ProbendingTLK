package probending;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Methods {
	
	public static Probending plugin;
	
	public Methods(Probending plugin) {
		Methods.plugin = plugin;
	}
	
	public static void configCheck() {
		FileConfiguration c = plugin.getConfig();
		
		c.addDefault("Storage.engine", "sqlite");
		c.addDefault("Storage.host", "localhost");
		c.addDefault("Storage.port", 3306);
		c.addDefault("Storage.database", "minecraft");
		c.addDefault("Storage.username", "root");
		c.addDefault("Storage.password", "");
		
		c.options().copyDefaults(true);
		plugin.saveConfig();
	}
	
	/*
	 * Everything below this line is for the Database Functions
	 */

    //Everything the database can do \/    
    public static void insertPlayerToDatabase(Player player){
    	DBConnection.sql.modifyQuery("INSERT INTO Probending_Players('uuid', 'playername', 'wins1', 'wins3', 'coins', 'rating') VALUES ('" + player.getUniqueId().toString() + "', '" + player.getName() + "', " + 0 + ", " + 0 + ", " + 0 + ", " + 1000 + ");");
    }
    
    public static int getWinScore1v1(Player player){
        int i = 0;
        ResultSet rs2 = DBConnection.sql.readQuery("SELECT * FROM Probending_Players WHERE playername = '" + player.getName() + "'");
        try {
        	if (rs2.next()) {
        		i = rs2.getInt("wins1");
        	}
        } catch (SQLException e) {
        	e.printStackTrace();
        }
        return i;

    }
    
    public static int getWinScore3v3(Player player){
        int i = 0;
        
        ResultSet rs2 = DBConnection.sql.readQuery("SELECT * FROM Probending_Players WHERE playername = '" + player.getName() + "'");
        try {
        	if (rs2.next()) {
        		i = rs2.getInt("wins3");
        	}
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return i;
    }
    
    public static int getCoins(Player player){
        int i = 0;
        ResultSet rs2 = DBConnection.sql.readQuery("SELECT * FROM Probending_Players WHERE playername = '" + player.getName() + "'");
        try {
        	if (rs2.next()) {
        		i = rs2.getInt("coins");
        	}
        } catch (SQLException e) {
        	e.printStackTrace();
        }
        return i;
    }
    
    public static int getRating(Player player){
        int i = 0;
        ResultSet rs2 = DBConnection.sql.readQuery("SELECT * FROM Probending_Players WHERE playername = '" + player.getName() + "'");
        try {
        	if (rs2.next()) {
        		i = rs2.getInt("rating");
        	}
        } catch (SQLException ex) {
        	ex.printStackTrace();
        }
        return i;
    }
    
    public static String getTeam(Player player){
        String team = null;
        ResultSet rs2 = DBConnection.sql.readQuery("SELECT * FROM Probending_Players WHERE playername = '" + player.getName() + "'");
        try {
        	if (rs2.next()) {
        		team = rs2.getString("team");
        	}
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (team == null) return "N/A";
        return team;
    }
    
    public static int getTeamGames(Player player){
        String team = getTeam(player);
        if (team == "N/A" || team == null) return 0;
        int teamGames = 0;
        ResultSet rs2 = DBConnection.sql.readQuery("SELECT * FROM Probending_Teams WHERE teamname = '" + team + "'");
        try {
        	if (rs2.next()) {
        		teamGames = rs2.getInt("games");
        	}
        } catch (Exception ex) {
        	ex.printStackTrace();   
        }
        return teamGames;
    }
    
    public static int getTeamWins(Player player){
        String team = getTeam(player);
        if (team == null || team == "N/A") return 0;
        int teamWins = 0;
        ResultSet rs2 = DBConnection.sql.readQuery("SELECT * FROM Probending_Teams WHERE teamname = '" + team + "'");
        try {
        	if (rs2.next()) {
        		teamWins = rs2.getInt("wins");
        	}
        } catch (SQLException ex) {
        	ex.printStackTrace();
        }
        return teamWins;
    }
    
    public static void addWin1(Player player){
    	int currWins = getWinScore1v1(player);
    	int newWins = currWins + 1;
    	DBConnection.sql.modifyQuery("UPDATE Probending_Players SET wins1 = " + newWins + " WHERE playername = '" + player.getName() + "'");
    }
    
    public static void addWin3(Player player){
    	int currWins = getWinScore3v3(player);
    	int newWins = currWins + 1;
    	DBConnection.sql.modifyQuery("UPDATE Probending_Players SET wins3 = " + newWins + " WHERE playername = '" + player.getName() + "'");
    }

}
