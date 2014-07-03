package probending;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

//Need another SAVING METHOD NOT IN THE config.yml!
public class PBWarps {

	private static Probending plugin;
    
    private Map<String, Location> teamSpawns = new HashMap<>();
    
    public PBWarps(Probending plugin){
    	PBWarps.plugin = plugin;
//        config = new PBConfigManager("warps");
        loadSpawns();
    }
    
    public Location getSpawn(String name) {
        name = name.toLowerCase();
        return teamSpawns.get(name);
    }

    public void setSpawn(String name, Location location) {
        name = name.toLowerCase();
        teamSpawns.put(name, location);
        saveSpawn(name);
    }
    
    private void saveSpawn(String name) {
        name = name.toLowerCase();
        plugin.getConfig().set("Warps." + name + ".game", locToString(teamSpawns.get(name)));
        plugin.saveConfig();
//        config.getConfig().set("spawns." + name + ".game", locToString(teamSpawns.get(name)));
//        config.saveConfig();
    }

    private void loadSpawn(String name) {
        name = name.toLowerCase();
        teamSpawns.put(name, stringToLoc(plugin.getConfig().getString("Warps." + name + ".game")));
//        teamSpawns.put(name,
//                stringToLoc(config.getConfig().getString("spawns." + name + ".game")));
    }

    private String locToString(Location location) {
        return location.getWorld().getName() + "|" + location.getX() + "|" + location.getY() + "|" + location.getZ();
    }

    private Location stringToLoc(String s) {
        String[] args = s.split("\\|");
        World world = Bukkit.getServer().getWorld(args[0]);
        Double x = Double.parseDouble(args[1]);
        Double y = Double.parseDouble(args[2]);
        Double z = Double.parseDouble(args[3]);
        return new Location(world, x, y, z);
    }

    private void loadSpawns() {
        loadSpawn("red1");  //The player 1 of red location on the field on start
        loadSpawn("blue1"); //The player 1 of blue location on the field on start
        loadSpawn("red2");  //The player 2 of red location on the field on start
        loadSpawn("blue2"); //The player 2 of blue location on the field on start
        loadSpawn("red3");  //The player 3 of red location on the field on start
        loadSpawn("blue3"); //The player 3 of blue location on the field on start
        loadSpawn("b1");    //Middle of blue field 1
        loadSpawn("b2");    //Middle of blue field 2
        loadSpawn("b3");    //Middle of blue field 3
        loadSpawn("r1");    //Middle of red field 1
        loadSpawn("r2");    //Middle of red field 2
        loadSpawn("r3");    //Middle of red field 3
        loadSpawn("dmb");   //DeathMatch blue
        loadSpawn("dmr");   //DeathMatch red
        loadSpawn("r4");    //Out of the game for red
        loadSpawn("b4");    //Out of the game for blue
        loadSpawn("spawn"); //The spawn location
        loadSpawn("change");//The teleport location for changing your element
    }
}
