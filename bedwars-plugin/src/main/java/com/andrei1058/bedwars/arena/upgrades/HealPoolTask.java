package com.andrei1058.bedwars.arena.upgrades;

import com.andrei1058.bedwars.BedWars;
import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.api.arena.team.ITeam;
import com.andrei1058.bedwars.api.configuration.ConfigPath;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static com.andrei1058.bedwars.BedWars.config;
import static com.andrei1058.bedwars.BedWars.plugin;

public class HealPoolTask extends BukkitRunnable {

    private ITeam bwt;
    private int maxX, minX, maxY, minY, maxZ, minZ;
    private IArena arena;
    private Random r = new Random();
    private Location l;

    private static List<HealPoolTask> healPoolTasks = new ArrayList<>();

    public HealPoolTask(ITeam bwt){
        this.bwt = bwt;
        if (bwt == null || bwt.getSpawn() == null){
            removeForTeam(this.bwt);
            cancel();
            return;
        }
        int radius = bwt.getArena().getConfig().getInt(ConfigPath.ARENA_ISLAND_RADIUS);
        Location teamspawn = bwt.getSpawn();
        this.maxX = (teamspawn.getBlockX() + radius);
        this.minX = (teamspawn.getBlockX() - radius);
        this.maxY = (teamspawn.getBlockY() + radius);
        this.minY = (teamspawn.getBlockY() - radius);
        this.maxZ = (teamspawn.getBlockZ() + radius);
        this.minZ = (teamspawn.getBlockZ() - radius);
        this.arena = bwt.getArena();
        this.runTaskTimerAsynchronously(plugin, 0, config.getInt(ConfigPath.GENERAL_CONFIGURATION_HEAL_POOL_PARTICLE_REFRESH_INTERVAL));
        healPoolTasks.add(this);
    }

    @Override
    public void run(){
        //null checks
        if ((bwt == null) || (bwt.getSpawn() == null) || (arena == null)){
            healPoolTasks.remove(this);
            return;
        }

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    l = new Location(arena.getWorld(), x + .5, y + .5, z +.5);
                    if (l.getBlock().getType() != Material.AIR) continue;
                    int chance = r.nextInt(config.getInt(ConfigPath.GENERAL_CONFIGURATION_HEAL_POOL_PARTICLE_SPARSITY));
                    if (chance == 0) {
                        if (config.getBoolean(ConfigPath.GENERAL_CONFIGURATION_HEAL_POOL_SEEN_TEAM_ONLY)) {
                            for (Player p : bwt.getMembers()) {
                                BedWars.nms.playVillagerEffect(p, l);
                            }
                        }
                        else
                        {
                            for (Player p : arena.getPlayers()) {
                                BedWars.nms.playVillagerEffect(p, l);
                            }
                        }
                    }
                }
            }
        }
    }

    public static boolean exists(IArena arena, ITeam bwt){
        if (healPoolTasks.isEmpty()) return false;
        for (HealPoolTask hpt : healPoolTasks) {
            if (hpt.getArena() == arena && hpt.getBwt() == bwt) return true;
        }
        return false;
    }

    /**
     Iterate through the list using an iterator and perform operations.
     Change the original for loop to use an iterator,
     to avoid throwing ConcurrentModificationException when removing elements from the list during the loop.
     @param a Need to remove the Arena of Healing Pool.
     */
    public static void removeForArena(IArena a){
        if (healPoolTasks.isEmpty() || a == null) return;
        Iterator<HealPoolTask> healPoolTasksIterator = healPoolTasks.iterator();
        while (healPoolTasksIterator.hasNext()) {
            HealPoolTask hpt = healPoolTasksIterator.next();
            if (hpt == null) continue;
            if (hpt.getArena().equals(a)){
                hpt.cancel();
                healPoolTasksIterator.remove();
            }
        }
    }

    public  static void removeForArena(String a){
        if (healPoolTasks == null || healPoolTasks.isEmpty() || (a == null)) return;
        Iterator<HealPoolTask> healPoolTasksIterator = healPoolTasks.iterator();
        while (healPoolTasksIterator.hasNext()) {
            HealPoolTask hpt = healPoolTasksIterator.next();
            if (hpt == null) continue;
            if (hpt.getArena().getWorldName().equals(a)){
                hpt.cancel();
                healPoolTasksIterator.remove();
            }
        }
    }

    public  static void removeForTeam(ITeam team){
        if (healPoolTasks == null || healPoolTasks.isEmpty()  || (team == null)) return;
        Iterator<HealPoolTask> healPoolTasksIterator = healPoolTasks.iterator();
        while (healPoolTasksIterator.hasNext()) {
            HealPoolTask hpt = healPoolTasksIterator.next();
            if (hpt == null) continue;
            if (hpt.getBwt().equals(team)){
                hpt.cancel();
                healPoolTasksIterator.remove();
            }
        }
    }

    public ITeam getBwt() {return bwt;}

    public IArena getArena() {return arena;}
}
