package com.andrei1058.bedwars.listeners;

import com.andrei1058.bedwars.api.events.gameplay.GameEndEvent;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GameEndListener implements Listener {

    @EventHandler
    public void cleanDroppedItems(@NotNull GameEndEvent event) {
        if (event.getArena().getPlayers().isEmpty()) {
            return;
        }

        // clear dropped items
        World game = event.getArena().getWorld();
        for (Entity item : game.getEntities()) {
            if (item instanceof Item || item instanceof ItemStack){
                item.remove();
            }
        }
    }
}
