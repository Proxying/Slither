package com.shmozo.slither.utils;

import com.shmozo.slither.SlitherIO;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by Kieran Quigley (Proxying) on 01-May-16 for CherryIO.
 */
public class BaseUtils {

    public static boolean isSlitherFood(ItemStack itemStack) {
        if (itemStack.getType() != Material.STAINED_CLAY && itemStack.getType() != Material.EMERALD_BLOCK && itemStack.getType() != Material.LAPIS_BLOCK
                && itemStack.getType() != Material.IRON_BLOCK && itemStack.getType() != Material.CHEST && itemStack.getType() != Material.ENDER_CHEST && itemStack.getType() != Material.TNT) {
            return false;
        }
        return true;
    }

    public static boolean isLargeFood(ItemStack itemStack) {
        return !itemStack.getEnchantments().isEmpty();
    }

    public static ItemStack createRandomSlitherFood() {
        ItemStack itemStack = new ItemStack(Material.STAINED_CLAY, 1, (short) SlitherIO.getInstance().getRandom().nextInt(15));
        if (SlitherIO.getInstance().getRandom().nextFloat() >= 0.75) {
            itemStack.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
        }
        return itemStack;
    }

    public static Location getBlockBehindLocation(Location location) {
        Vector inverseDirectionVec = location.getDirection().multiply(-1);
        return location.add(inverseDirectionVec);
    }

    public static void sendActionBar(Player player, String message) {
        try {
            Class<?> c1 = Class.forName("org.bukkit.craftbukkit." + SlitherIO.getInstance().getNmsVers() + ".entity.CraftPlayer");
            Object p = c1.cast(player);
            Object ppoc;
            Class<?> c4 = Class.forName("net.minecraft.server." + SlitherIO.getInstance().getNmsVers() + ".PacketPlayOutChat");
            Class<?> c5 = Class.forName("net.minecraft.server." + SlitherIO.getInstance().getNmsVers() + ".Packet");
            if ((SlitherIO.getInstance().getNmsVers().equalsIgnoreCase("v1_8_R1") || !SlitherIO.getInstance().getNmsVers().startsWith("v1_8_")) && !SlitherIO.getInstance().getNmsVers().startsWith("v1_9_")) {
                Class<?> c2 = Class.forName("net.minecraft.server." + SlitherIO.getInstance().getNmsVers() + ".ChatSerializer");
                Class<?> c3 = Class.forName("net.minecraft.server." + SlitherIO.getInstance().getNmsVers() + ".IChatBaseComponent");
                Method m3 = c2.getDeclaredMethod("a", String.class);
                Object cbc = c3.cast(m3.invoke(c2, "{\"text\": \"" + message + "\"}"));
                ppoc = c4.getConstructor(new Class<?>[]{c3, byte.class}).newInstance(cbc, (byte) 2);
            } else {
                Class<?> c2 = Class.forName("net.minecraft.server." + SlitherIO.getInstance().getNmsVers() + ".ChatComponentText");
                Class<?> c3 = Class.forName("net.minecraft.server." + SlitherIO.getInstance().getNmsVers() + ".IChatBaseComponent");
                Object o = c2.getConstructor(new Class<?>[]{String.class}).newInstance(message);
                ppoc = c4.getConstructor(new Class<?>[]{c3, byte.class}).newInstance(o, (byte) 2);
            }
            Method m1 = c1.getDeclaredMethod("getHandle");
            Object h = m1.invoke(p);
            Field f1 = h.getClass().getDeclaredField("playerConnection");
            Object pc = f1.get(h);
            Method m5 = pc.getClass().getDeclaredMethod("sendPacket", c5);
            m5.invoke(pc, ppoc);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static short getRandomColor() {
        switch (SlitherIO.getInstance().getRandom().nextInt(10)) {
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return 6;
            case 3:
                return 3;
            case 4:
                return 4;
            case 5:
                return 5;
            case 6:
                return 7;
            case 7:
                return 10;
            case 8:
                return 11;
            case 9:
                return 14;
            case 10:
                return 13;
            default:
                return 5;
        }
    }

    public static ChatColor getChatColor(short color) {
        switch (color) {
            case 0:
                return ChatColor.WHITE;
            case 1:
                return ChatColor.GOLD;
            case 3:
                return ChatColor.AQUA;
            case 4:
                return ChatColor.YELLOW;
            case 5:
                return ChatColor.GREEN;
            case 6:
                return ChatColor.LIGHT_PURPLE;
            case 7:
                return ChatColor.GRAY;
            case 10:
                return ChatColor.DARK_PURPLE;
            case 11:
                return ChatColor.BLUE;
            case 13:
                return ChatColor.DARK_GREEN;
            case 14:
                return ChatColor.RED;
            default:
                return ChatColor.WHITE;
        }
    }

    public static short getRainbowColorIndex(int rainbowColorCount) {
        if (rainbowColorCount % 6 == 0) {
            return (short) 0;
        } else if (rainbowColorCount % 5 == 0) {
            return (short) 14;
        } else if (rainbowColorCount % 4 == 0) {
            return (short) 4;
        } else if (rainbowColorCount % 3 == 0) {
            return (short) 11;
        } else if (rainbowColorCount % 2 == 0) {
            return (short) 6;
        } else {
            return (short) 5;
        }
    }

    /**
     * Get an itemStack from the base64String
     *
     * @param base42String
     * @return
     * @since 1.0
     */
    public static ItemStack itemStackFromBase64(String base42String) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(base42String));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack items = (ItemStack) dataInput.readObject();

            dataInput.close();
            return items;
        } catch (ClassNotFoundException e) {
            try {
                throw new IOException("Unable to decode class type.", e);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Convert one (1) item to base64 serialized String.
     *
     * @param item
     * @return
     * @throws IllegalStateException
     * @since 1.0
     */
    public static String itemStackToBase64(ItemStack item) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeObject(item);

            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }
}