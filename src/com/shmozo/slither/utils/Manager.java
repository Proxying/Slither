package com.shmozo.slither.utils;

import com.shmozo.shmeeb.board.BoardBuilder;
import com.shmozo.shmeeb.inventory.Menu;
import com.shmozo.shmeeb.player.User;
import com.shmozo.slither.SlitherIO;
import com.shmozo.slither.enums.EnumSnakeSkins;
import com.shmozo.slither.objects.SlitherPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import java.util.Collections;

/**
 * Created by Kieran Quigley (Proxying) on 01-May-16 for CherryIO.
 */
public class Manager {

    public static void updateActionBars() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(SlitherIO.getInstance(), () -> Bukkit.getOnlinePlayers().forEach(player -> {
            SlitherPlayer sPlayer = SlitherIO.getInstance().getSlitherPlayers().get(player.getName());
            if (sPlayer.isAlive()) {
                BaseUtils.sendActionBar(player, ChatColor.AQUA + "Score" + ChatColor.GRAY + ": " + ChatColor.GREEN + sPlayer.getPlayerScore()
                        + ChatColor.GRAY + " || " + ChatColor.AQUA + "Size" + ChatColor.GRAY + ": " + ChatColor.GREEN + sPlayer.getPlayerSize());
            }
        }), 0L, 4L);
    }

    public static void updateScoreboards() {
        Bukkit.getScheduler().runTaskTimer(SlitherIO.getInstance(), () -> Bukkit.getOnlinePlayers().forEach(player -> {
            SlitherPlayer sPlayer = SlitherIO.getInstance().getSlitherPlayers().get(player.getName());
            Scoreboard scoreboard;
            Objective objective;
            if (sPlayer.getScoreboard() != null) {
                scoreboard = SlitherIO.getInstance().getSlitherPlayers().get(player.getName()).getScoreboard();
                objective = scoreboard.getObjective("test");
            } else {
                scoreboard = new BoardBuilder(ChatColor.AQUA + ChatColor.BOLD.toString() + "SLITHER").setDisplaySlot(DisplaySlot.SIDEBAR).setScore("    ", 1).setScore(ChatColor.YELLOW + "www.shmozo.com", 0).getBoard();
                objective = scoreboard.getObjective("test");
                sPlayer.setScoreboard(scoreboard);
            }
            int max = 5;
            if (SlitherIO.getInstance().getSlitherList().size() < 5) {
                max = SlitherIO.getInstance().getSlitherList().size();
            }
            scoreboard.getEntries().stream().filter(entry -> !entry.equals(ChatColor.YELLOW + "www.shmozo.com") && !entry.equals("    ")).forEach(scoreboard::resetScores);
            for (int i = 0; i < max; i++) {
                SlitherPlayer sP = SlitherIO.getInstance().getSlitherList().get(i);
                if (sP.isAlive() && sP.getPlayerScore() > 10) {
                    objective.getScore(sP.getPlayerName()).setScore(sP.getPlayerScore());
                }
            }
            player.setScoreboard(scoreboard);
        }), 0L, 15L);
    }

    public static void updateTopPlayerList() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(SlitherIO.getInstance(), () -> Collections.sort(SlitherIO.getInstance().getSlitherList(), (SlitherPlayer sP1, SlitherPlayer sP2) -> sP1.getPlayerScore() - sP2.getPlayerScore()), 0L, 30L);
    }

    public static void handlePlayerMovement() {
        Bukkit.getScheduler().runTaskTimer(SlitherIO.getInstance(), () -> Bukkit.getOnlinePlayers().forEach(player -> {
            if (player.getGameMode() == GameMode.ADVENTURE) {
                SlitherPlayer sPlayer = SlitherIO.getInstance().getSlitherPlayers().get(player.getName());
                if (sPlayer.isAlive()) {
                    Vector fixedPitchDirection = new Vector();
                    double rotX = (double) player.getLocation().getYaw();
                    fixedPitchDirection.setY(-Math.sin(Math.toRadians(25.0)));
                    double xz = Math.cos(Math.toRadians(25.0));
                    fixedPitchDirection.setX(-xz * Math.sin(Math.toRadians(rotX)));
                    fixedPitchDirection.setZ(xz * Math.cos(Math.toRadians(rotX)));
                    if (player.isSneaking()) {
                        if (sPlayer.getPlayerScore() > 25) {
                            sPlayer.boost();
                            player.setVelocity(fixedPitchDirection.multiply(.575));
                        } else {
                            sPlayer.getSnakeObject().disableBoostColors();
                            player.setVelocity(fixedPitchDirection.multiply(.325));
                        }
                    } else {
                        sPlayer.getSnakeObject().disableBoostColors();
                        player.setVelocity(fixedPitchDirection.multiply(.325));
                    }
                }
            }
        }), 0, 1);
    }

    public static void handlePlayerSpawnProtection() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(SlitherIO.getInstance(), () -> SlitherIO.getInstance().getSpawnProtection().stream().filter(SlitherPlayer::isAlive).forEach(sP -> {
            if (sP.getProtectionTime() >= 1) {
                sP.reduceProtectionTime(1);
            } else {
                SlitherIO.getInstance().getSpawnProtection().remove(sP);
            }
        }), 0L, 20L);
    }

    public static void openMainMenu(Player player) {
        if (SlitherIO.getInstance().getMainMenu() == null) {
            Menu menu = new Menu("Slither.io Menu", 9) {
                @Override
                public void onClick(InventoryClickEvent inventoryClickEvent, int i) {
                    switch (i) {
                        case 0:
                            if (!SlitherIO.getInstance().getSlitherPlayers().get(inventoryClickEvent.getWhoClicked().getName()).isAlive()) {
                                inventoryClickEvent.getWhoClicked().teleport(inventoryClickEvent.getWhoClicked().getWorld().getSpawnLocation());
                                SlitherIO.getInstance().getSlitherPlayers().get(inventoryClickEvent.getWhoClicked().getName()).respawnPlayer();
                                inventoryClickEvent.getWhoClicked().closeInventory();
                            }
                            break;
                        case 4:
                            openSkinMenu((Player) inventoryClickEvent.getWhoClicked());
                            break;
                        case 8:
                            new User(inventoryClickEvent.getWhoClicked().getUniqueId()).proxyTo("lobby");
                            break;
                    }
                }
                @Override
                public void onClose(InventoryCloseEvent inventoryCloseEvent) {
                }
            }.setItem(0, new ItemBuilder().setItem(Material.STAINED_GLASS_PANE, (short) 5, ChatColor.RESET + "Start Game", new String[]{}).build())
                    .setItem(4, new ItemBuilder().setItem(Material.STAINED_GLASS_PANE, (short) 3, ChatColor.RESET + "Skin Selection", new String[]{}).build())
                    .setItem(8, new ItemBuilder().setItem(Material.STAINED_GLASS_PANE, (short) 14, ChatColor.RESET + "Exit To Lobby", new String[]{}).build());
            SlitherIO.getInstance().setMainMenu(menu);
            menu.openFor(player);
        } else {
            SlitherIO.getInstance().getMainMenu().openFor(player);
        }
    }

    public static void openSkinMenu(Player player) {
        if (SlitherIO.getInstance().getSkinMenu() == null) {
            Menu menu = new Menu("Skin Selection", 18) {
                @Override
                public void onClick(InventoryClickEvent inventoryClickEvent, int i) {
                    switch (i) {
                        case 0:
                            openSkinPurchaseMenu((Player) inventoryClickEvent.getWhoClicked());
                            break;
                        case 4:
                            openSkinEquipMenu((Player) inventoryClickEvent.getWhoClicked());
                            break;
                        case 8:
                            openSkinRentMenu((Player) inventoryClickEvent.getWhoClicked());
                            break;
                        case 14:
                            openMainMenu((Player) inventoryClickEvent.getWhoClicked());
                            break;
                    }
                }
                @Override
                public void onClose(InventoryCloseEvent inventoryCloseEvent) {
                }
            }.setItem(0, new ItemBuilder().setItem(Material.STAINED_GLASS_PANE, (short) 5, ChatColor.RESET + "Purchase Skins", new String[]{}).build())
                    .setItem(4, new ItemBuilder().setItem(Material.STAINED_GLASS_PANE, (short) 3, ChatColor.RESET + "Equip Skins", new String[]{}).build())
                    .setItem(8, new ItemBuilder().setItem(Material.STAINED_GLASS_PANE, (short) 14, ChatColor.RESET + "Rent Skins", new String[]{}).build())
                    .setItem(13, new ItemBuilder().setItem(new ItemStack(Material.BARRIER), ChatColor.RESET + "Main Menu", new String[]{}).build());
            SlitherIO.getInstance().setSkinMenu(menu);
            menu.openFor(player);
        } else {
            SlitherIO.getInstance().getSkinMenu().openFor(player);
        }
    }

    public static void openSkinPurchaseMenu(Player player) {
        Inventory inventory = Bukkit.getServer().createInventory(null, 18, "Purchase Skins");
        for (EnumSnakeSkins skin : EnumSnakeSkins.values()) {
            //TODO: Check if player already owns skin. If so, don't display.
            inventory.addItem(new ItemBuilder().setItem(new ItemStack(Material.BEACON), ChatColor.RESET + skin.name(), new String[]{
                    "Purchase Price : " + skin.getPurchasePrice()}).build());
            }
        inventory.setItem(13, new ItemBuilder().setItem(new ItemStack(Material.BARRIER), ChatColor.RESET + "Skin Menu", new String[]{}).build());
        player.openInventory(inventory);
    }

    public static void openSkinRentMenu(Player player) {
        Inventory inventory = Bukkit.getServer().createInventory(null, 18, "Rent Skins");
        for (EnumSnakeSkins skin : EnumSnakeSkins.values()) {
            //TODO: Check if player already owns skin. If so, don't display.
            inventory.addItem(new ItemBuilder().setItem(new ItemStack(Material.WOOD_HOE), ChatColor.RESET + skin.name(), new String[]{
                    "Rent Price : " + skin.getRentPrice()}).build());
        }
        inventory.setItem(13, new ItemBuilder().setItem(new ItemStack(Material.BARRIER), ChatColor.RESET + "Skin Menu", new String[]{}).build());
        player.openInventory(inventory);
    }

    public static void openSkinEquipMenu(Player player) {
        Inventory inventory = Bukkit.getServer().createInventory(null, 18, "Equip Skins");
        for (EnumSnakeSkins skin : EnumSnakeSkins.values()) {
            //TODO: Check if player owns skin. If not, don't display
            inventory.addItem(new ItemBuilder().setItem(new ItemStack(Material.GOLDEN_APPLE), ChatColor.RESET + skin.name(), new String[]{}).build());
        }
        inventory.setItem(13, new ItemBuilder().setItem(new ItemStack(Material.BARRIER), ChatColor.RESET + "Skin Menu", new String[]{}).build());
        player.openInventory(inventory);
    }

    public static void loginTasks(Player player) {
        SlitherPlayer sP = new SlitherPlayer(player);
        SlitherIO.getInstance().getSlitherPlayers().put(player.getName(), sP);
        Bukkit.getScheduler().scheduleSyncDelayedTask(SlitherIO.getInstance(), () -> Manager.openMainMenu(player), 1L);
    }

    public static void logoutTasks(Player player) {
        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(potionEffect.getType());
        }
        SlitherIO.getInstance().getSlitherPlayers().get(player.getName()).killPlayer(true);
    }
}
