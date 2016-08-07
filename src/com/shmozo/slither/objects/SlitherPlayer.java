package com.shmozo.slither.objects;

import com.shmozo.shmeeb.player.User;
import com.shmozo.slither.SlitherIO;
import com.shmozo.slither.shop.ShopHandler;
import com.shmozo.slither.utils.BaseUtils;
import com.shmozo.slither.utils.Manager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;

import java.util.UUID;

/**
 * Created by Kieran Quigley (Proxying) on 01-May-16 for CherryIO.
 */
public class SlitherPlayer {

    private UUID uuid;
    private String playerName;
    private int playerScore;
    private int playerSize;
    private Scoreboard scoreboard;
    private World world;
    private int smallFoods;
    private int largeFoods;
    private int boostCount;
    private ChatColor chatColor;
    private boolean isAlive;
    private PlayerStats playerStats;
    private SnakeObject snakeObject;
    private String partyName;
    private int protectionTime;
    private int moveTaskRepeats;

    public SlitherPlayer(Player player) {
        this.uuid = player.getUniqueId();
        this.playerStats = new PlayerStats(getUuid());
        this.snakeObject = new SnakeObject(getUuid(), player.getWorld(), this);
        this.playerName = player.getName();
        this.playerScore = 100;
        this.playerSize = 1;
        this.scoreboard = null;
        this.chatColor = BaseUtils.getChatColor(getSnakeObject().getSkinColor());
        this.world = player.getWorld();
        this.smallFoods = 0;
        this.largeFoods = 0;
        this.boostCount = 0;
        this.isAlive = false;
        this.partyName = "Test1";
        this.protectionTime = 5;
        getSnakeObject().setupPlayerHead();
        SlitherIO.getInstance().getSlitherList().add(this);
        ShopHandler.getRainbowSnakes().add(this);
    }

    public int getPlayerSize() {
        return playerSize;
    }

    public int getPlayerScore() {
        return playerScore;
    }

    public String getPlayerName() {
        return playerName;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public World getWorld() {
        return world;
    }

    public void addPlayerSize(int amount) {
        playerSize += amount;
    }

    public void addPlayerScore(int amount) {
        playerScore += amount;
    }

    public void removePlayerSize(int amount) {
        playerSize -= amount;
    }

    public void removePlayerScore(int amount) {
        playerScore -= amount;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public ChatColor getChatColor() {
        return chatColor;
    }

    public int getSmallFoods() {
        return smallFoods;
    }

    public int getLargeFoods() {
        return largeFoods;
    }

    public int getBoostCount() {
        return boostCount;
    }

    public PlayerStats getPlayerStats() {
        return playerStats;
    }

    public SnakeObject getSnakeObject() {
        return snakeObject;
    }

    public String getPartyName() {
        return partyName;
    }

    public int getProtectionTime() {
        return protectionTime;
    }

    public void reduceProtectionTime(int amount) {
        if (protectionTime < 1) return;
        this.protectionTime -= amount;
    }

    public int getMoveTaskRepeats() {
        return moveTaskRepeats;
    }

    public void setMoveTaskRepeats(int moveTaskRepeats) {
        this.moveTaskRepeats = moveTaskRepeats;
    }

    public void incrementMoveTaskRepeats(int amount) {
        this.moveTaskRepeats += amount;
    }

    public void boost() {
        removePlayerScore(5);
        boostCount++;
        getSnakeObject().enableBoostColors();
        if (boostCount % 3 == 0) {
            ItemStack itemStack = getWorld().dropItem(getSnakeObject().getSnakeBody().get(getSnakeObject().getSnakeBody().size() - 1).getEyeLocation(), getSnakeObject().getSnakeBody().get(getSnakeObject().getSnakeBody().size() - 1).getHelmet()).getItemStack();
            itemStack.removeEnchantment(Enchantment.ARROW_DAMAGE);
        } else if (boostCount % 13 == 0) {
            getSnakeObject().removeFollowingArmorStand();
            removePlayerSize(1);
        }
    }

    public void eatSmallFood() {
        addPlayerScore(5);
        smallFoods++;
        if (smallFoods % 8 == 0) {
            getSnakeObject().addFollowingArmorStand();
            addPlayerSize(1);
        }
    }

    public void eatLargeFood() {
        addPlayerScore(15);
        largeFoods++;
        if (largeFoods % 4 == 0) {
            getSnakeObject().addFollowingArmorStand();
            addPlayerSize(1);
        }
    }

    public void respawnPlayer() {
        playerSize = 1;
        smallFoods = 0;
        largeFoods = 0;
        boostCount = 0;
        protectionTime = 5;
        playerScore = 100;
        User user = new User(getUuid());
        user.setCustomName(getPlayerName());
        user.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 2));
        isAlive = true;
        getSnakeObject().spawnSnake();
        SlitherIO.getInstance().getSpawnProtection().add(this);
    }

    public void setScoreboard(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    public void killPlayer(boolean disconnect) {
        updateFields();
        if (!disconnect) {
            isAlive = false;
            this.protectionTime = 5;
            this.playerScore = 100;
            this.playerSize = 1;
            this.smallFoods = 0;
            this.largeFoods = 0;
            this.boostCount = 0;
            getSnakeObject().killSnake();
            getSnakeObject().getSnakeBody().clear();
            Manager.openMainMenu(Bukkit.getPlayer(getUuid()));
        } else {
            if (isAlive()) {
                getSnakeObject().removeSnake();
            }
            getPlayerStats().updateRedis();
            if (ShopHandler.getRainbowSnakes().contains(this)) {
                ShopHandler.getRainbowSnakes().remove(this);
            }
            SlitherIO.getInstance().getSlitherList().remove(this);
            SlitherIO.getInstance().getSlitherPlayers().remove(playerName);
            if (SlitherIO.getInstance().getSpawnProtection().contains(this)) {
                SlitherIO.getInstance().getSpawnProtection().remove(this);
            }
        }
    }

    private void updateFields() {
        getPlayerStats().setCachedTotalScore(getPlayerStats().getCachedTotalScore() + getPlayerScore());
        getPlayerStats().setCachedTotalSize(getPlayerStats().getCachedTotalSize() + getPlayerSize());
        getPlayerStats().setCachedTotalSmallFoods(getPlayerStats().getCachedTotalSmallFoods() + getSmallFoods());
        getPlayerStats().setCachedTotalLargeFoods(getPlayerStats().getCachedTotalLargeFoods() + getLargeFoods());
        getPlayerStats().setCachedTotalBoostTime(getPlayerStats().getCachedTotalBoostTime() + getBoostCount() / 20);
        getPlayerStats().setCachedTotalDeaths(getPlayerStats().getCachedTotalDeaths() + 1);
        if (getPlayerStats().getCachedHighestScore() < getPlayerScore()) {
            getPlayerStats().setCachedHighestScore(getPlayerScore());
        }
        if (getPlayerStats().getCachedHighestSize() < getPlayerSize()) {
            getPlayerStats().setCachedHighestSize(getPlayerSize());
        }
        if (getPlayerStats().getCachedHighestSmallFoods() < getSmallFoods()) {
            getPlayerStats().setCachedHighestSmallFoods(getSmallFoods());
        }
        if (getPlayerStats().getCachedHighestLargeFoods() < getLargeFoods()) {
            getPlayerStats().setCachedHighestLargeFoods(getLargeFoods());
        }
        if (getPlayerStats().getCachedHighestBoostTime() < (getBoostCount() / 20)) {
            getPlayerStats().setCachedHighestBoostTime(getBoostCount() / 20);
        }
    }

    public void handleSkinUpdates() {
        if (isAlive()) {
            getSnakeObject().rainbowSkin();
        }
    }
}
