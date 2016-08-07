package com.shmozo.slither.enums;

/**
 * Created by Kieran Quigley (Proxying) on 16-May-16 for CherryIO.
 */
public enum EnumSnakeSkins {
    DEFAULT(0, "DEFAULT", 0, 0),
    RAINBOW(1, "RAINBOW", 0, 0),
    EMERALD(2, "EMERALD", 50, 500),
    LAPIS(3, "LAPIS", 50, 500),
    IRON(4, "IRON", 50, 500),
    CHEST(5, "CHEST", 50, 500),
    ENDERCHEST(6, "ENDERCHEST", 0, 0),
    TNT(7, "TNT", 50, 500);

    private int id;
    private String rawName;
    private int rentPrice;
    private int purchasePrice;

    EnumSnakeSkins(int id, String rawName, int rentPrice, int purchasePrice) {
        this.id = id;
        this.rawName = rawName;
        this.rentPrice = rentPrice;
        this.purchasePrice = purchasePrice;
    }

    public static EnumSnakeSkins getById(int id) {
        for (EnumSnakeSkins skinType : values()) {
            if (skinType.id == id) {
                return skinType;
            }
        }
        return null;
    }

    public static EnumSnakeSkins getByName(String rawName) {
        for (EnumSnakeSkins skinType : values()) {
            if (skinType.rawName.equalsIgnoreCase(rawName)) {
                return skinType;
            }
        }
        return null;
    }

    public int getPurchasePrice() {
        return purchasePrice;
    }

    public int getRentPrice() {
        return rentPrice;
    }

    public String getRawName() {
        return rawName;
    }

}
