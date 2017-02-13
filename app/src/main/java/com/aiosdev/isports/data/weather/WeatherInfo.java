package com.aiosdev.isports.data.weather;

import java.io.Serializable;
import java.util.List;

/**
 * 城市天气信息显示类
 * 包括4个子类
 */

public class WeatherInfo implements Serializable {
    private List<Weather> mWeather;
    private Main mMain;
    private Wind mWind;
    private Clouds mClouds;
    private String name;

    public List<Weather> getWeather() {
        return mWeather;
    }

    public void setWeather(List<Weather> weather) {
        mWeather = weather;
    }

    public Main getMain() {
        return mMain;
    }

    public void setMain(Main main) {
        mMain = main;
    }

    public Wind getWind() {
        return mWind;
    }

    public void setWind(Wind wind) {
        mWind = wind;
    }

    public Clouds getClouds() {
        return mClouds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setClouds(Clouds clouds) {
        mClouds = clouds;
    }

    @Override
    public String toString() {
        return "WeatherInfo{" +
                "mWeather=" + mWeather +
                ", mMain=" + mMain +
                ", mWind=" + mWind +
                ", mClouds=" + mClouds +
                ", mName=" + name +
                '}';
    }
}
