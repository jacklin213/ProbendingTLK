package probending;

import probending.Storage.MySQL;
import probending.Storage.Database;
import probending.Storage.SQLite;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class DBConnection {
	
	public static Database sql;
	static Probending plugin = Probending.getInstance();
	
	public static String host;
	public static int port;
	public static String db;
	public static String user;
	public static String pass;


	public static void init() {
		if (plugin.getConfig().getString("Storage.engine").equalsIgnoreCase("mysql")) {
			sql = new MySQL(Probending.logger, "[Probending] Establishing MySQL Connection...", host, port, user, pass, db);
			((MySQL) sql).open();
			Probending.logger.info("[Probending] Database connection established.");
			
			if (!sql.tableExists("Probending_Players")) {
				String query = "CREATE TABLE `Probending_Players` ("
						+ "`id` int (32) NOT NULL AUTO_INCREMENT,"
						+ "`uuid` varchar(255),"
						+ "`playername` varchar(255),"
						+ "`wins1` int(32),"
						+ "`wins3` int(32),"
						+ "`teamname` varchar(255),"
						+ "`coins` int(32),"
						+ "`rating` int(32),"
						+ " PRIMARY KEY (id));";
				sql.modifyQuery(query);
			}
			if (!sql.tableExists("Probending_Teams")) {
				String query = "CREATE TABLE `Probending_Teams` ("
						+ "`id` int (32) NOT NULL AUTO_INCREMENT,"
						+ "`teamname` varchar(255),"
						+ "`player1` varchar(255),"
						+ "`player2` varchar(255),"
						+ "`player3` varchar(255),"
						+ "`player4` varchar(255),"
						+ "`games` int(32),"
						+ "`wins` int(32),"
						+ "`rating` int(32),"
						+ " PRIMARY KEY (id));";
				sql.modifyQuery(query);
			}
		} else { // We'll default to SQLite
			sql = new SQLite(Probending.logger, "[Probending] Establishing SQLite Connection.", "probending.db", Probending.instance.getDataFolder().getAbsolutePath());
			((SQLite) sql).open();
			
			if (!sql.tableExists("Probending_Players")) {
				String query = "CREATE TABLE `Probending_Players` ("
						+ "`id` INTEGER PRIMARY KEY,"
						+ "`uuid` TEXT(255),"
						+ "`playername` TEXT(255),"
						+ "`wins1` INTEGER(32),"
						+ "`wins3` INTEGER(32),"
						+ "`teamname` TEXT(255),"
						+ "`coins` INTEGER(32),"
						+ "`rating` INTEGER(32));";
				sql.modifyQuery(query);
			}
			if (!sql.tableExists("Probending_Teams")) {
				String query = "CREATE TABLE `Probending_Teams` ("
						+ "`id` INTEGER PRIMARY KEY,"
						+ "`teamname` TEXT(255),"
						+ "`player1` TEXT(255),"
						+ "`player2` TEXT(255),"
						+ "`player3` TEXT(255),"
						+ "`player4` TEXT(255),"
						+ "`games` INTEGER(32),"
						+ "`wins` INTEGER(32),"
						+ "`rating` INTEGER(32));";
				sql.modifyQuery(query);
			}
		}

	}

}
