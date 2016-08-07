package com.shmozo.slither.shop;

import com.shmozo.slither.SlitherIO;
import com.shmozo.slither.objects.SlitherPlayer;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kieran Quigley (Proxying) on 15-May-16 for CherryIO.
 */
public class ShopHandler {

    private static List<SlitherPlayer> rainbowSnakes = new ArrayList<>();

    public static List<SlitherPlayer> getRainbowSnakes() {
        return rainbowSnakes;
    }

    public static void handlePurchasedSkins() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(SlitherIO.getInstance(), () -> getRainbowSnakes().stream().forEach(SlitherPlayer::handleSkinUpdates), 0, 4L);
    }
}
