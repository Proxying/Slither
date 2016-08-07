package com.shmozo.slither.objects;

import com.shmozo.shmeeb.player.User;

import java.util.UUID;

/**
 * Created by Kieran Quigley (Proxying) on 15-May-16 for CherryIO.
 */
public class PlayerStats {

    private User user;
    private int cachedTotalScore;
    private int cachedTotalSize;
    private int cachedTotalSmallFoods;
    private int cachedTotalLargeFoods;
    private int cachedTotalBoostTime;
    private int cachedTotalDeaths;
    private int cachedHighestScore;
    private int cachedHighestSize;
    private int cachedHighestSmallFoods;
    private int cachedHighestLargeFoods;
    private int cachedHighestBoostTime;
    private String cachedSerializedPlayerHead;

    public PlayerStats(UUID uuid) {
        user = new User(uuid);
        if (user.exist("slither:firstJoin")) {
            cachedTotalScore = Integer.valueOf(user.getCached("slither:totalScore"));
            cachedTotalSize = Integer.valueOf(user.getCached("slither:totalSize"));
            cachedTotalSmallFoods = Integer.valueOf(user.getCached("slither:totalSmallFoods"));
            cachedTotalLargeFoods = Integer.valueOf(user.getCached("slither:totalLargeFoods"));
            cachedTotalBoostTime = Integer.valueOf(user.getCached("slither:totalBoostTime"));
            cachedTotalDeaths = Integer.valueOf(user.getCached("slither:totalDeaths"));
            cachedHighestScore = Integer.valueOf(user.getCached("slither:highestScore"));
            cachedHighestSize = Integer.valueOf(user.getCached("slither:highestSize"));
            cachedHighestSmallFoods = Integer.valueOf(user.getCached("slither:highestSmallFoods"));
            cachedHighestLargeFoods = Integer.valueOf(user.getCached("slither:highestLargeFoods"));
            cachedHighestBoostTime = Integer.valueOf(user.getCached("slither:highestBoostTime"));
            if (user.exist("slither:serializedPlayerHead")) {
                cachedSerializedPlayerHead = user.getCached("slither:serializedPlayerHead");
            }
        }
    }

    public void updateRedis() {
        if (!user.exist("slither:firstJoin")) {
            user.setCache("slither:firstJoin", String.valueOf(System.currentTimeMillis()));
        }
        if (!user.exist("slither:serializedPlayerHead")) {
            user.setCache("slither:serializedPlayerHead", cachedSerializedPlayerHead);
            user.setExpire("slither:serializedPlayerHead", 300);
        }
        user.setCache("slither:totalScore", String.valueOf(cachedTotalScore));
        user.setCache("slither:totalSize", String.valueOf(cachedTotalSize));
        user.setCache("slither:totalSmallFoods", String.valueOf(cachedTotalSmallFoods));
        user.setCache("slither:totalLargeFoods", String.valueOf(cachedTotalLargeFoods));
        user.setCache("slither:totalBoostTime", String.valueOf(cachedTotalBoostTime / 20));
        user.setCache("slither:totalDeaths", String.valueOf(cachedTotalDeaths));
        user.setCache("slither:highestScore", String.valueOf(cachedHighestScore));
        user.setCache("slither:highestSize", String.valueOf(cachedHighestSize));
        user.setCache("slither:highestSmallFoods", String.valueOf(cachedHighestSmallFoods));
        user.setCache("slither:highestLargeFoods", String.valueOf(cachedHighestLargeFoods));
        user.setCache("slither:highestBoostTime", String.valueOf(cachedHighestBoostTime));
    }

    public int getCachedHighestSize() {
        return cachedHighestSize;
    }

    public void setCachedHighestSize(int cachedHighestSize) {
        this.cachedHighestSize = cachedHighestSize;
    }

    public int getCachedTotalScore() {
        return cachedTotalScore;
    }

    public void setCachedTotalScore(int cachedTotalScore) {
        this.cachedTotalScore = cachedTotalScore;
    }

    public int getCachedTotalSize() {
        return cachedTotalSize;
    }

    public void setCachedTotalSize(int cachedTotalSize) {
        this.cachedTotalSize = cachedTotalSize;
    }

    public int getCachedTotalSmallFoods() {
        return cachedTotalSmallFoods;
    }

    public void setCachedTotalSmallFoods(int cachedTotalSmallFoods) {
        this.cachedTotalSmallFoods = cachedTotalSmallFoods;
    }

    public int getCachedTotalLargeFoods() {
        return cachedTotalLargeFoods;
    }

    public void setCachedTotalLargeFoods(int cachedTotalLargeFoods) {
        this.cachedTotalLargeFoods = cachedTotalLargeFoods;
    }

    public int getCachedTotalBoostTime() {
        return cachedTotalBoostTime;
    }

    public void setCachedTotalBoostTime(int cachedTotalBoostTime) {
        this.cachedTotalBoostTime = cachedTotalBoostTime;
    }

    public int getCachedTotalDeaths() {
        return cachedTotalDeaths;
    }

    public void setCachedTotalDeaths(int cachedTotalDeaths) {
        this.cachedTotalDeaths = cachedTotalDeaths;
    }

    public int getCachedHighestScore() {
        return cachedHighestScore;
    }

    public void setCachedHighestScore(int cachedHighestScore) {
        this.cachedHighestScore = cachedHighestScore;
    }

    public int getCachedHighestSmallFoods() {
        return cachedHighestSmallFoods;
    }

    public void setCachedHighestSmallFoods(int cachedHighestSmallFoods) {
        this.cachedHighestSmallFoods = cachedHighestSmallFoods;
    }

    public int getCachedHighestLargeFoods() {
        return cachedHighestLargeFoods;
    }

    public void setCachedHighestLargeFoods(int cachedHighestLargeFoods) {
        this.cachedHighestLargeFoods = cachedHighestLargeFoods;
    }

    public int getCachedHighestBoostTime() {
        return cachedHighestBoostTime;
    }

    public void setCachedHighestBoostTime(int cachedHighestBoostTime) {
        this.cachedHighestBoostTime = cachedHighestBoostTime;
    }

    public void setSerializedPlayerHead(String serializedPlayerHead) {
        this.cachedSerializedPlayerHead = serializedPlayerHead;
    }

    public String getSerializedPlayerHead() {
        return cachedSerializedPlayerHead;
    }
}
