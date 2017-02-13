package com.aiosdev.isports.data.weather;

/**
 * Created by Administrator on 2016/12/11 0011.
 */

public class Wind {
    private String speed;
    private String deg;

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getDeg() {
        return deg;
    }

    public void setDeg(String deg) {
        this.deg = deg;
    }

    @Override
    public String toString() {
        return "Wind{" +
                "speed='" + speed + '\'' +
                ", deg='" + deg + '\'' +
                '}';
    }
}
