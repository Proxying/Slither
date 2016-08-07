package com.shmozo.slither.objects;

import com.shmozo.shmeeb.player.User;
import com.shmozo.slither.SlitherIO;
import com.shmozo.slither.enums.EnumSnakeSkins;
import com.shmozo.slither.utils.BaseUtils;
import com.shmozo.slither.utils.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.EulerAngle;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Kieran Quigley (Proxying) on 16-May-16 for CherryIO.
 */
public class SnakeObject {

    private List<ArmorStand> snakeBody;
    private ArmorStand snakeHead;
    private World world;
    private User user;
    private short skinColor;
    private EnumSnakeSkins skinType;
    private double snakeHeadStaticY;
    private double snakeBodyStaticY;
    private int rainbowUpdateIteration;
    private boolean isBoosting;
    private SlitherPlayer slitherPlayer;
    private ItemStack snakeHeadItem = null;
    private int rainbowColorCounter;

    public SnakeObject(UUID uuid, World world, SlitherPlayer slitherPlayer) {
        this.snakeBody = new ArrayList<>();
        this.world = world;
        this.user = new User(uuid);
        this.skinColor = BaseUtils.getRandomColor();
        this.skinType = EnumSnakeSkins.DEFAULT;
        this.rainbowUpdateIteration = 0;
        this.rainbowColorCounter = SlitherIO.getInstance().getRandom().nextInt(5);
        this.slitherPlayer = slitherPlayer;
    }

    public void setupPlayerHead() {
        if (getSlitherPlayer().getPlayerStats().getSerializedPlayerHead() == null) {
            snakeHeadItem = new ItemBuilder().createSkull(getSlitherPlayer().getPlayerName()).build();
            getSlitherPlayer().getPlayerStats().setSerializedPlayerHead(BaseUtils.itemStackToBase64(snakeHeadItem));
        } else {
            snakeHeadItem = BaseUtils.itemStackFromBase64(getSlitherPlayer().getPlayerStats().getSerializedPlayerHead());
        }
    }

    public void spawnSnake() {
        snakeHead = getWorld().spawn(user.getPlayer().getEyeLocation().subtract(0, 2.4, 0), ArmorStand.class);
        if (getSnakeHeadItem() == null) {
            setupPlayerHead();
        }
        snakeHead.setHelmet(getSnakeHeadItem());
        snakeHead.setVisible(false);
        snakeHead.setBasePlate(false);
        snakeHead.setHeadPose(new EulerAngle(3.14, 3.14, 3.14));
        snakeHead.setGravity(false);
        snakeHead.setRemoveWhenFarAway(false);
        snakeHead.setCanPickupItems(false);
        snakeHead.setMetadata("Owner", new FixedMetadataValue(SlitherIO.getInstance(), user.getPlayer().getName()));
        snakeHead.setMetadata("Party", new FixedMetadataValue(SlitherIO.getInstance(), "NONE"));
        snakeHeadStaticY = snakeHead.getLocation().getY();

        ArmorStand armorStand = getWorld().spawn(BaseUtils.getBlockBehindLocation(user.getPlayer().getEyeLocation().subtract(0, 1.9, 0)), ArmorStand.class);
        armorStand.setHelmet(getCorrectSkinBlock());
        armorStand.setVisible(false);
        armorStand.setSmall(true);
        armorStand.setBasePlate(false);
        armorStand.setGravity(false);
        armorStand.setRemoveWhenFarAway(false);
        armorStand.setCanPickupItems(false);
        armorStand.setHeadPose(new EulerAngle(3.14, 3.14, 3.14));
        armorStand.setMetadata("Owner", new FixedMetadataValue(SlitherIO.getInstance(), user.getPlayer().getName()));
        armorStand.setMetadata("Party", new FixedMetadataValue(SlitherIO.getInstance(), "NONE"));
        snakeBodyStaticY = armorStand.getLocation().getY();
        getSnakeBody().add(armorStand);
        addFollowingArmorStand();
        addFollowingArmorStand();
        addFollowingArmorStand();
    }

        public void killSnake() {
        this.isBoosting = false;
        ItemStack head = getWorld().dropItem(getSnakeHead().getEyeLocation(), new ItemStack(Material.STAINED_CLAY, 1, getSkinColor())).getItemStack();
        head.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
        getSnakeHead().remove();
        this.snakeHead = null;
        for (ArmorStand armorStand : getSnakeBody()) {
            ItemStack itemStack = getWorld().dropItem(armorStand.getEyeLocation(), armorStand.getHelmet()).getItemStack();
            itemStack.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
            armorStand.remove();
        }
        getSnakeBody().clear();
    }

    public void removeSnake() {
        getSnakeHead().remove();
        getSnakeBody().stream().forEach(ArmorStand::remove);
        getSnakeBody().clear();
    }

    public void addFollowingArmorStand() {
        if (!getSnakeBody().isEmpty()) {
            Location location = BaseUtils.getBlockBehindLocation(getSnakeBody().get(getSnakeBody().size() - 1).getLocation());
            location.setY(snakeBodyStaticY);
            ArmorStand armorStand = getWorld().spawn(location, ArmorStand.class);
            armorStand.setHelmet(getCorrectSkinBlock());
            armorStand.setVisible(false);
            armorStand.setSmall(true);
            armorStand.setBasePlate(false);
            armorStand.setGravity(false);
            armorStand.setRemoveWhenFarAway(false);
            armorStand.setCanPickupItems(false);
            armorStand.setHeadPose(new EulerAngle(3.14, 3.14, 3.14));
            armorStand.setMetadata("Owner", new FixedMetadataValue(SlitherIO.getInstance(), user.getPlayer().getName()));
            armorStand.setMetadata("Party", new FixedMetadataValue(SlitherIO.getInstance(), "NONE"));
            getSnakeBody().add(armorStand);
        }
    }

    public void removeFollowingArmorStand() {
        if (!getSnakeBody().isEmpty()) {
            ArmorStand armorStand =  getSnakeBody().get(getSnakeBody().size() - 1);
            getWorld().dropItem(armorStand.getEyeLocation(), armorStand.getHelmet());
            getSnakeBody().remove(armorStand);
            armorStand.remove();
        }
    }

    public void enableBoostColors() {
        if (!isBoosting) {
            ItemStack helmet;
            for (ArmorStand stand : getSnakeBody()) {
                helmet = stand.getHelmet();
                helmet.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
                stand.setHelmet(helmet);
            }
            isBoosting = true;
        }
    }

    public void disableBoostColors() {
        if (isBoosting) {
            ItemStack helmet;
            for (ArmorStand stand : getSnakeBody()) {
                helmet = stand.getHelmet();
                helmet.removeEnchantment(Enchantment.ARROW_DAMAGE);
                stand.setHelmet(helmet);
            }
            isBoosting = false;
        }
    }

    public void rainbowSkin() {
        if (rainbowUpdateIteration > getSnakeBody().size() - 1) {
            rainbowUpdateIteration = 0;
        }
        ArmorStand stand = getSnakeBody().get(rainbowUpdateIteration);
        if (rainbowUpdateIteration == getSnakeBody().size() - 1) {
            stand.setHelmet(getSnakeBody().get(0).getHelmet());
        } else {
            stand.setHelmet(getSnakeBody().get(rainbowUpdateIteration + 1).getHelmet());
        }
        rainbowUpdateIteration++;
    }

    public boolean isFriendlySnake(ArmorStand stand) {
        if (!stand.hasMetadata("Owner") || !stand.hasMetadata("Party")) {
            return false;
        }
        if (stand.getMetadata("Owner").get(0).asString().equals(getUser().getPlayer().getName())) {
            return true;
        }
        if (stand.getMetadata("Party").get(0).asString().equals("NONE")) {
            return false;
        }
        if (stand.getMetadata("Party").get(0).asString().equals(getSlitherPlayer().getPartyName())) {
            return true;
        }
        return false;
    }

    public ItemStack getCorrectSkinBlock() {
        switch (getSkinType()) {
            case DEFAULT:
                return new ItemStack(Material.STAINED_CLAY, 1, getSkinColor());
            case RAINBOW:
                rainbowColorCounter++;
                return new ItemStack(Material.STAINED_CLAY, 1, BaseUtils.getRainbowColorIndex(rainbowColorCounter));
            case EMERALD:
                return new ItemStack(Material.EMERALD_BLOCK, 1);
            case LAPIS:
                return new ItemStack(Material.LAPIS_BLOCK, 1);
            case IRON:
                return new ItemStack(Material.IRON_BLOCK, 1);
            case CHEST:
                return new ItemStack(Material.CHEST, 1);
            case ENDERCHEST:
                return new ItemStack(Material.ENDER_CHEST, 1);
            case TNT:
                return new ItemStack(Material.TNT, 1);
            default:
                return new ItemStack(Material.STAINED_CLAY, 1, getSkinColor());
        }
    }

    public World getWorld() {
        return world;
    }

    public ArmorStand getSnakeHead() {
        return snakeHead;
    }

    public List<ArmorStand> getSnakeBody() {
        return snakeBody;
    }

    public EnumSnakeSkins getSkinType() {
        return skinType;
    }

    public User getUser() {
        return user;
    }

    public short getSkinColor() {
        return skinColor;
    }

    public double getSnakeHeadStaticY() {
        return snakeHeadStaticY;
    }

    public double getSnakeBodyStaticY() {
        return snakeBodyStaticY;
    }

    public SlitherPlayer getSlitherPlayer() {
        return slitherPlayer;
    }

    public ItemStack getSnakeHeadItem() {
        return snakeHeadItem;
    }

    public void setSkinType(EnumSnakeSkins skinType) {
        this.skinType = skinType;
    }
}
