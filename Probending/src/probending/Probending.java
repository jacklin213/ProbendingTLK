package probending;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Probending extends JavaPlugin{
	
	public static Probending instance;
    public static final Logger logger = Logger.getLogger("minecraft");
    private PBGameStart gamestart;
    private PBScoreBoard scoreboard;
    private PBTeleporter teleporter;
    private static Probending plugin;
    private WorldEditPlugin worldEdit;
    private WorldGuardPlugin worldGuard;
    private DBConnection pbdatabase;
    
    //A fix for the teleporter&gamestart bad coding :/
    @Override
    public void onEnable() {
    	instance = this;
    	new Methods(this);
        Methods.configCheck();
        PluginDescriptionFile pdfFile = this.getDescription();
        Probending.logger.log(Level.INFO, ChatColor.GREEN + "{0} has been enabled!", pdfFile.getName());
        worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        worldGuard = (WorldGuardPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
//        configManager = new PBConfigManager("config"); 
//        getDatabaseInfo();
        scoreboard = new PBScoreBoard(pbdatabase);
        gamestart = new PBGameStart(this, scoreboard);
        teleporter = new PBTeleporter(scoreboard, gamestart);
        gamestart.setTeleporter(teleporter);
        getServer().getPluginManager().registerEvents(new PBListener(scoreboard, gamestart, teleporter, pbdatabase), this);
        
        DBConnection.host = getConfig().getString("Storage.host");
        DBConnection.port = getConfig().getInt("Storage.port");
        DBConnection.db = getConfig().getString("Storage.database");
        DBConnection.user = getConfig().getString("Storage.user");
        DBConnection.pass = getConfig().getString("Storage.password");
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) { 
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("spawn")) {
                gamestart.tryLeaveQueue(player);
                teleporter.teleportSpawn(player);
            } else if (cmd.getName().equalsIgnoreCase("probending")) {
                for(int i = 0; i < args.length; i++){
                    args[i] = args[i].toLowerCase();
                }
//============================================================================= 0 arguments! ===========================================================
                if (args.length == 0) {
                     showChatInfoBegin(player);
//============================================================================= 1 arguments! ===========================================================
                } else if (args.length == 1) {
                    switch (args[0]) {
                        case "team":
                            showChatInfoTeam(player);
                            break;
                        case "ranking":
                            showChatInfoRanking(player);
                            break;
                        case "join":
                            if (scoreboard.objectiveWaiting.getScore(player).getScore() >= 1){
                                player.sendMessage(ChatColor.RED + "You have already been queued or are in game!");
                            } else {
                                gamestart.tryJoinGame(player, "");
                            }
                            break;
                        case "leave":
                            gamestart.tryLeaveQueue(player);
                            break;
                        case "test":
                            break;
                        case "change":
                            gamestart.tryLeaveQueue(player);
                            teleporter.teleportElementChange(player);
                            break;
                        default:
                            showChatInfoBegin(player);
                            break;
                    }
                } else if (args.length == 2) {
                    switch (args[0]) {
                        case "team":
                            switch (args[1]) {
                                case "create":
                                    player.sendMessage(ChatColor.DARK_RED + "oOo " + ChatColor.DARK_AQUA + "Probending Team Configuration" + ChatColor.DARK_RED + " oOo");
                                    player.sendMessage(ChatColor.AQUA + "Try: " + ChatColor.BLUE + "/probending team create " + ChatColor.RED + "[name]");
                                    break;
                                case "invite":
                                    player.sendMessage(ChatColor.DARK_RED + "oOo " + ChatColor.DARK_AQUA + "Probending Team Configuration" + ChatColor.DARK_RED + " oOo");
                                    player.sendMessage(ChatColor.AQUA + "Try: " + ChatColor.BLUE + "/probending team invite " + ChatColor.RED + "[player]");
                                    break;
                                case "kick":
                                    player.sendMessage(ChatColor.DARK_RED + "oOo " + ChatColor.DARK_AQUA + "Probending Team Configuration" + ChatColor.DARK_RED + " oOo");
                                    player.sendMessage(ChatColor.AQUA + "Try: " + ChatColor.BLUE + "/probending team kick " + ChatColor.RED + "[player]");
                                    break;
                                default:
                                    showChatInfoTeam(player);
                                    break;
                                }
                            break;
                        case "join":
                            switch (args[1]) {
                                case "1v1":
                                    gamestart.tryJoinGame(player, "1v1");
                                    break;
                                case "3v3":
                                    gamestart.tryJoinGame(player, "3v3");
                                    break;
                                default:
                                    player.sendMessage(ChatColor.DARK_RED + "Sorry but this is not a mode to play on!");
                                    break;
                            }
                            break;
                        case "ranking":
                            if ("you".equals((args[1]))){
                                
                            } else {
                                showChatInfoRanking(player);
                            }
                            break;
                        case "setspawn":
                            if (player.isOp() || player.hasPermission("probending.setspawn")){
                                teleporter.setSpawn(args[1], player.getLocation());
                                player.sendMessage(ChatColor.BLUE + "SetSpawn for: " + args[1]);
                            } else
                                player.sendMessage(ChatColor.DARK_RED + "Sorry but your name is not runefist, is it?!");
                            break;
                        default:
                            showChatInfoBegin(player);
                            break;
                    }
                } else if (args.length == 3) {
                    if ("team".equals(args[0])) {
                        switch (args[1]) {
                            case "create":
                                break;
                            case "invite":
                                break;
                            case "kick":
                                break;
                            default:
                                showChatInfoTeam(player);
                                break;
                        }
                    } else {
                        showChatInfoBegin(player);
                    }
                }
//============================================================================= + arguments! ===========================================================
                else {
                    player.sendMessage(ChatColor.DARK_RED + "To many arguments!");
                    player.sendMessage(ChatColor.AQUA + "Need help? Try: " + ChatColor.BLUE + "/probending");
                }
            }
        }
        return false;
    }
    
    private void showChatInfoBegin(Player player) {
        player.sendMessage(ChatColor.DARK_RED + "oOo " + ChatColor.RED + "Probending");
        player.sendMessage(ChatColor.DARK_AQUA + "=" + ChatColor.AQUA + "=" + ChatColor.DARK_AQUA + "= " + ChatColor.BLUE + "/probending team");
        player.sendMessage(ChatColor.DARK_AQUA + "=" + ChatColor.AQUA + "=" + ChatColor.DARK_AQUA + "= " + ChatColor.BLUE + "/probending ranking");
        player.sendMessage(ChatColor.DARK_AQUA + "=" + ChatColor.AQUA + "=" + ChatColor.DARK_AQUA + "= " + ChatColor.BLUE + "/probending join" + ChatColor.AQUA + " [1v1/3v3]");
        player.sendMessage(ChatColor.DARK_AQUA + "=" + ChatColor.AQUA + "=" + ChatColor.DARK_AQUA + "= " + ChatColor.BLUE + "/probending leave");
        player.sendMessage(ChatColor.DARK_AQUA + "=" + ChatColor.AQUA + "=" + ChatColor.DARK_AQUA + "= " + ChatColor.BLUE + "/probending change");
    }
    
    private void showChatInfoTeam(Player player) {
        player.sendMessage(ChatColor.DARK_RED + "oOo " + ChatColor.DARK_AQUA + "Probending Team Configuration" + ChatColor.DARK_RED + " oOo");
        player.sendMessage(ChatColor.DARK_AQUA + "=" + ChatColor.AQUA + "=" + ChatColor.DARK_AQUA + "= " + ChatColor.BLUE + "/probending team "
                + "create " + ChatColor.AQUA + "[name]");
        player.sendMessage(ChatColor.DARK_AQUA + "=" + ChatColor.AQUA + "=" + ChatColor.DARK_AQUA + "= " + ChatColor.BLUE + "/probending team "
                + "invite " + ChatColor.AQUA + "[player]");
        player.sendMessage(ChatColor.DARK_AQUA + "=" + ChatColor.AQUA + "=" + ChatColor.DARK_AQUA + "= " + ChatColor.BLUE + "/probending team "
                + "info " + ChatColor.GRAY + "[teamname]");
        player.sendMessage(ChatColor.DARK_AQUA + "=" + ChatColor.AQUA + "=" + ChatColor.DARK_AQUA + "= " + ChatColor.BLUE + "/probending team "
                + "kick " + ChatColor.AQUA + "[player]");
        player.sendMessage(ChatColor.DARK_AQUA + "=" + ChatColor.AQUA + "=" + ChatColor.DARK_AQUA + "= " + ChatColor.BLUE + "/probending team "
                + "leave");
        player.sendMessage(ChatColor.DARK_AQUA + "=" + ChatColor.AQUA + "=" + ChatColor.DARK_AQUA + "= " + ChatColor.BLUE + "/probending team "
                + "disband " + ChatColor.DARK_GRAY + "[teamname]");
    }
    
    private void showChatInfoRanking(Player player) {
        player.sendMessage(ChatColor.DARK_RED + "oOo " + ChatColor.DARK_AQUA + "Probending Rankings" + ChatColor.DARK_RED + " oOo");
        player.sendMessage(ChatColor.DARK_AQUA + "=" + ChatColor.AQUA + "=" + ChatColor.DARK_AQUA + "= " + ChatColor.BLUE + "/probending ranking "
                + "you");
        player.sendMessage(ChatColor.DARK_AQUA + "=" + ChatColor.AQUA + "=" + ChatColor.DARK_AQUA + "= " + ChatColor.BLUE + "/probending ranking "
                + "[name] ");
    }
    
    @Override
    public void onDisable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        this.logger.log(Level.INFO, ChatColor.RED + "{0} has been disabled!", pdfFile.getName());
    }
    
    public static Probending getInstance() {
    	return Probending.instance;
    }
    
 }
