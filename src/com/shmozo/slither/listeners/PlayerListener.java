package com.shmozo.slither.listeners;

import com.shmozo.slither.SlitherIO;
import com.shmozo.slither.objects.SlitherPlayer;
import com.shmozo.slither.utils.BaseUtils;
import com.shmozo.slither.utils.Manager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;

/**
 * Created by Kieran Quigley (Proxying) on 01-May-16 for CherryIO.
 */
public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() != null && event.getRightClicked() instanceof ArmorStand) {
            if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerEatItem(PlayerPickupItemEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.ADVENTURE) return;
        event.setCancelled(true);
        if (!SlitherIO.getInstance().getSlitherPlayers().get(event.getPlayer().getName()).isAlive()) return;
        event.getItem().remove();
        if (BaseUtils.isSlitherFood(event.getItem().getItemStack())) {
            if (BaseUtils.isLargeFood(event.getItem().getItemStack())) {
                //Add large food points
                SlitherIO.getInstance().getSlitherPlayers().get(event.getPlayer().getName()).eatLargeFood();
            } else {
                SlitherIO.getInstance().getSlitherPlayers().get(event.getPlayer().getName()).eatSmallFood();
                //Add small food points
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.ADVENTURE) return;
        if (!SlitherIO.getInstance().getSlitherPlayers().get(event.getPlayer().getName()).isAlive()) return;
        if (event.getTo().distanceSquared(event.getFrom()) == 0) {
            return;
        }
        if (event.getTo().getBlock().getRelative(BlockFace.DOWN).getType() == Material.REDSTONE_BLOCK) {
            SlitherIO.getInstance().getSlitherPlayers().get(event.getPlayer().getName()).killPlayer(false);
            return;
        }
        if (event.getPlayer().getNearbyEntities(0.9, 1.3, 0.9).stream().anyMatch(ArmorStand.class::isInstance)) {
            ArmorStand stand = (ArmorStand) event.getPlayer().getNearbyEntities(0.9, 1.3, 0.9).stream().filter(e -> e instanceof ArmorStand).findFirst().get();
            if (SlitherIO.getInstance().getSlitherPlayers().get(event.getPlayer().getName()).getProtectionTime() <= 0) {
                if (!SlitherIO.getInstance().getSlitherPlayers().get(event.getPlayer().getName()).getSnakeObject().isFriendlySnake(stand)) {
                    //Player has collided with another player's tail and should be killed.
                    SlitherIO.getInstance().getSlitherPlayers().get(event.getPlayer().getName()).killPlayer(false);
                    return;
                }
            }
        }
        SlitherPlayer sPlayer = SlitherIO.getInstance().getSlitherPlayers().get(event.getPlayer().getName());
        if (sPlayer.getMoveTaskRepeats() == 20) {
            sPlayer.setMoveTaskRepeats(0);
        }
        if (sPlayer.getMoveTaskRepeats() == 9 || sPlayer.getMoveTaskRepeats() == 18) {
            sPlayer.incrementMoveTaskRepeats(1);
            return;
        } else {
            sPlayer.incrementMoveTaskRepeats(1);
        }
        Location newLoc;
        for (int i = (sPlayer.getSnakeObject().getSnakeBody().size() - 1); i >= 0; i--) {
            ArmorStand stand = sPlayer.getSnakeObject().getSnakeBody().get(i);
            if (i == 0) {
                newLoc = sPlayer.getSnakeObject().getSnakeHead().getLocation();
                newLoc.setY(sPlayer.getSnakeObject().getSnakeBodyStaticY());
                stand.teleport(newLoc);
            } else {
                newLoc = sPlayer.getSnakeObject().getSnakeBody().get((i - 1)).getLocation();
                stand.teleport(newLoc);
            }
        }
        newLoc = event.getTo().clone();
        newLoc.setY(sPlayer.getSnakeObject().getSnakeHeadStaticY());
        sPlayer.getSnakeObject().getSnakeHead().teleport(newLoc);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void setupPlayerObject(PlayerJoinEvent event) {
        event.getPlayer().teleport(event.getPlayer().getWorld().getSpawnLocation());
        event.getPlayer().setGameMode(GameMode.ADVENTURE);
        Manager.loginTasks(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void cleanPlayerObjectQuit(PlayerQuitEvent event) {
        Manager.logoutTasks(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onFoodChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    /*@EventHandler(priority = EventPriority.NORMAL)
    public void onSkinMenuClick(InventoryClickEvent event) {
        if (!(event.getInventory().getTitle().equals("Skin Selection"))) return;
        if (event.getRawSlot() > 27) return;
        event.setCancelled(true);
        if (EnumSnakeSkins.getById(event.getRawSlot()) == null) return;
        SlitherPlayer slitherPlayer = SlitherIO.getInstance().getSlitherPlayers().get(event.getWhoClicked().getName());
        slitherPlayer.getSnakeObject().setSkinType(EnumSnakeSkins.getById(event.getRawSlot()));
        event.getWhoClicked().sendMessage("You equipped the " + EnumSnakeSkins.getById(event.getRawSlot()).toString() + " snake skin!");
        Manager.openMainMenu((Player) event.getWhoClicked());
    }*/
}
