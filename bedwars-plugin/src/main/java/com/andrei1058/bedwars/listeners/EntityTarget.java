package com.andrei1058.bedwars.listeners;

import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.arena.Arena;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityTarget  implements Listener {

    public static Map<Player, List<Entity>> entityTargetRecords = new HashMap<>();

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        Entity entity = event.getEntity();
        Entity target = event.getTarget();

        if (target instanceof Player) {
            Player player = (Player) target;
            IArena arena = Arena.getArenaByPlayer(player);

            if (arena.isReSpawning(player))
            {
                event.setCancelled(true);
                return;
            }

            if (!(entityTargetRecords.containsKey(player) && entityTargetRecords.get(player).contains(entity)))
            {
                List<Entity> hatePlayerEntitys = entityTargetRecords.getOrDefault(player, new ArrayList<>());
                hatePlayerEntitys.add(entity);
                entityTargetRecords.put(player, hatePlayerEntitys);
            }
        }
    }
}