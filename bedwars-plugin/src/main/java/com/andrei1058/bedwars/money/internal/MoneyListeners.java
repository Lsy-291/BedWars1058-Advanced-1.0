package com.andrei1058.bedwars.money.internal;

import com.andrei1058.bedwars.BedWars;
import com.andrei1058.bedwars.api.arena.team.ITeam;
import com.andrei1058.bedwars.api.events.gameplay.GameEndEvent;
import com.andrei1058.bedwars.api.events.player.PlayerBedBreakEvent;
import com.andrei1058.bedwars.api.events.player.PlayerKillEvent;
import com.andrei1058.bedwars.api.language.Language;
import com.andrei1058.bedwars.api.language.Messages;
import com.andrei1058.bedwars.configuration.MoneyConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class MoneyListeners implements Listener {

    /**
     * Create a new winner / loser money reward.
     */
    @EventHandler
    public void onGameEnd(GameEndEvent e) {
        int moneyRewardPerTeammateSurvival = MoneyConfig.money.getInt("money-rewards.per-teammate");
        int moneyRewardGameVictory = MoneyConfig.money.getInt("money-rewards.game-win");
        e.getArena().getAllPlayers().forEach(player -> {
            if (player != null) {
                UUID playerUUID = player.getUniqueId();
                if (e.getAliveWinners().contains(playerUUID) && moneyRewardGameVictory > 0)
                {
                    BedWars.getEconomy().giveMoney(player, moneyRewardGameVictory);
                    player.sendMessage(Language.getMsg(player, Messages.MONEY_REWARD_WIN).replace("{money}", String.valueOf(moneyRewardGameVictory)));
                }

                ITeam team = e.getArena().getExTeam(playerUUID);
                int teamMemberCount = team.getMembers().size();
                if (teamMemberCount > 1) {
                    if (moneyRewardPerTeammateSurvival > 0) {
                        int teammateRewards = moneyRewardPerTeammateSurvival * teamMemberCount;
                        BedWars.getEconomy().giveMoney(player, teammateRewards);
                        player.sendMessage(Language.getMsg(player, Messages.MONEY_REWARD_PER_TEAMMATE).replace("{money}", String.valueOf(teammateRewards)));
                    }
                }

            }
        });
    }

    /**
     * Create a new bed destroyed money reward.
     */
    @EventHandler
    public void onBreakBed(PlayerBedBreakEvent e) {
        Player player = e.getPlayer ();
        if (player == null) return;
        int bedDestroy = MoneyConfig.money.getInt("money-rewards.bed-destroyed");
        if (bedDestroy > 0) {
            BedWars.getEconomy().giveMoney(player, bedDestroy);
            player.sendMessage(Language.getMsg(player, Messages.MONEY_REWARD_BED_DESTROYED).replace("{money}", String.valueOf(bedDestroy)));
        }
    }

    /**
     * Create a kill money reward.
     */
    @EventHandler
    public void onKill(PlayerKillEvent e) {
        Player player = e.getKiller();
        Player victim = e.getVictim();
        if (player == null || victim.equals(player)) return;
        int finalKill = MoneyConfig.money.getInt("money-rewards.final-kill");
        int regularKill = MoneyConfig.money.getInt("money-rewards.regular-kill");
        if (e.getCause().isFinalKill()) {
            if (finalKill > 0) {
                BedWars.getEconomy().giveMoney(player, finalKill);
                player.sendMessage(Language.getMsg(player, Messages.MONEY_REWARD_FINAL_KILL).replace("{money}", String.valueOf(finalKill)));
            }
        } else {
            if (regularKill > 0) {
                BedWars.getEconomy().giveMoney(player, regularKill);
                player.sendMessage(Language.getMsg(player, Messages.MONEY_REWARD_REGULAR_KILL).replace("{money}", String.valueOf(regularKill)));
            }
        }
    }
}