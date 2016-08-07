package com.shmozo.slither.enums;

/**
 * Created by Kieran Quigley (Proxying) on 20-May-16 for CherryIO.
 */
public enum EnumSkinOwnage {
    UNOWNED(0, "UNOWNED"),
    PURCHASED(1, "PURCHASED"),
    RENTED(2, "RENTED");

    private int id;
    private String rawName;

    EnumSkinOwnage(int id, String rawName) {
        this.id = id;
        this.rawName = rawName;
    }

    public static EnumSkinOwnage getById(int id) {
        for (EnumSkinOwnage ownType : values()) {
            if (ownType.id == id) {
                return ownType;
            }
        }
        return null;
    }

    public static EnumSkinOwnage getByName(String rawName) {
        for (EnumSkinOwnage ownType : values()) {
            if (ownType.rawName.equalsIgnoreCase(rawName)) {
                return ownType;
            }
        }
        return null;
    }

}
