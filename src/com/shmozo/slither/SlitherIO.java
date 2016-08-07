package com.shmozo.slither;

import com.shmozo.shmeeb.inventory.Menu;
import com.shmozo.slither.listeners.PlayerListener;
import com.shmozo.slither.objects.SlitherPlayer;
import com.shmozo.slither.shop.ShopHandler;
import com.shmozo.slither.utils.Manager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Kieran Quigley (Proxying) on 01-May-16 for CherryIO.
 */
public class SlitherIO extends JavaPlugin {

    private static SlitherIO instance = null;

    private static Random random = new Random();

    private Map<String, SlitherPlayer> slitherPlayers = new HashMap<>();

    private List<SlitherPlayer> slitherList = new ArrayList<>();

    private CopyOnWriteArrayList<SlitherPlayer> spawnProtection = new CopyOnWriteArrayList<>();

    private String nmsVers;

    private Menu mainMenu;

    private Menu skinMenu;

    public void onEnable() {
        instance = this;
        nmsVers = Bukkit.getServer().getClass().getPackage().getName();
        nmsVers = nmsVers.substring(nmsVers.lastIndexOf(".") + 1);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Manager.updateTopPlayerList();
        Manager.updateScoreboards();
        Manager.updateActionBars();
        Manager.handlePlayerMovement();
        Manager.handlePlayerSpawnProtection();
        ShopHandler.handlePurchasedSkins();
    }

    public void onDisable() {
        for (SlitherPlayer sP : slitherPlayers.values()) {
            sP.killPlayer(true);
        }
    }

    public static SlitherIO getInstance() {
        return instance;
    }

    public Map<String, SlitherPlayer> getSlitherPlayers() {
        return slitherPlayers;
    }

    public Random getRandom() {
        return random;
    }

    public String getNmsVers(){
        return nmsVers;
    }

    public List<SlitherPlayer> getSlitherList() {
        return slitherList;
    }

    public CopyOnWriteArrayList<SlitherPlayer> getSpawnProtection() {
        return spawnProtection;
    }

    public Menu getMainMenu() {
        return mainMenu;
    }

    public void setMainMenu(Menu mainMenu) {
        this.mainMenu = mainMenu;
    }

    public Menu getSkinMenu() {
        return skinMenu;
    }

    public void setSkinMenu(Menu skinMenu) {
        this.skinMenu = skinMenu;
    }
}
