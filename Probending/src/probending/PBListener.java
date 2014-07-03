package probending;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import com.mewin.WGRegionEvents.events.RegionEnterEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PBListener implements Listener{
    PBScoreBoard scoreboard;
    PBTeleporter setplaces;
    PBGameStart gamestart;
    DBConnection database;
    //int iREevents = 0;
    
    public PBListener(PBScoreBoard scoreboard, PBGameStart gamestart, PBTeleporter setplaces, DBConnection database) {
        this.scoreboard = scoreboard;
        this.gamestart = gamestart;
        this.setplaces = setplaces;
        this.database = database;
    }
    
    //Method below does not work, find another solution to prevent bending before battle!
    /*
    @EventHandler
    public void onPlayerAnimation(PlayerAnimationEvent event) {
        if (scoreboard.board.getPlayerTeam(event.getPlayer()).getName() == "red" 
                || scoreboard.board.getPlayerTeam(event.getPlayer()).getName() == "blue" 
                && gamestart.preGame() == true){
            event.setCancelled(true);
        }
    }
    //*/
    
    @EventHandler//(event = PlayerJoinEvent.class, priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event) {
        scoreboard.addPlayerToScoreboard(event.getPlayer());
        event.getPlayer().getInventory().clear();
        event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 18000 * 12, 4));
        event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 18000 * 12, 4));
        event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 18000 * 12, 4));
        if(!event.getPlayer().hasPlayedBefore()) {
            Methods.insertPlayerToDatabase(event.getPlayer());
            setplaces.teleportElementChange(event.getPlayer());
        } else {
            setplaces.teleportSpawn(event.getPlayer());
        }
    }
    
    @EventHandler//(event = PlayerQuitEvent.class, priority = EventPriority.LOW)
    public void onPlayerQuit(PlayerQuitEvent event){
        gamestart.outOfGame(event.getPlayer(), gamestart.gameType());
    }
    
    @EventHandler
    public void playerEnterRegion(RegionEnterEvent event) {
        //Region restore
        if ("pbfield".equals(event.getRegion().getId()) || "r1".equals(event.getRegion().getId()) 
                    || "r2".equals(event.getRegion().getId())  || "r3".equals(event.getRegion().getId()) 
                    || "r4".equals(event.getRegion().getId()) || "b1".equals(event.getRegion().getId()) 
                    || "b2".equals(event.getRegion().getId())  || "b3".equals(event.getRegion().getId()) 
                    || "restore".equals(event.getRegion().getId())) {
            setplaces.gamePlaces(event.getPlayer(), event.getRegion().getId(), gamestart.gameType());
        }
    }
    
    @EventHandler
    public void PlayerMoveEvent(PlayerMoveEvent event) {
        gamestart.MayWalk(event.getPlayer());
    }
    
    @EventHandler
    public void Respawn(PlayerRespawnEvent event){
        event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 18000 * 12, 4));
        event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 18000 * 12, 4));
        event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 18000 * 12, 4));
        setplaces.teleportSpawn(event.getPlayer());
    }
}
