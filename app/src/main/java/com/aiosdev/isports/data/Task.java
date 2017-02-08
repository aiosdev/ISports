package com.aiosdev.isports.data;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/2/3 0003.
 */

public class Task implements Serializable {

    private String date;//日期
    private String taskNo;//任务编号
    private int step;//步数
    private Float distance;//距离
    private Float calories;//热量
    private int duration;//时长
    private int avg_step;//平均步幅
    private Float avg_speed;//平均速度
    private Float high_speed;//最高速度
    private Float low_speed;//最低速度

    public Task() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTaskNo() {
        return taskNo;
    }

    public void setTaskNo(String taskNo) {
        this.taskNo = taskNo;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public Float getCalories() {
        return calories;
    }

    public void setCalories(Float calories) {
        this.calories = calories;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getAvg_step() {
        return avg_step;
    }

    public void setAvg_step(int avg_step) {
        this.avg_step = avg_step;
    }

    public Float getAvg_speed() {
        return avg_speed;
    }

    public void setAvg_speed(Float avg_speed) {
        this.avg_speed = avg_speed;
    }

    public Float getHigh_speed() {
        return high_speed;
    }

    public void setHigh_speed(Float high_speed) {
        this.high_speed = high_speed;
    }

    public Float getLow_speed() {
        return low_speed;
    }

    public void setLow_speed(Float low_speed) {
        this.low_speed = low_speed;
    }

    @Override
    public String toString() {
        return "Task{" +
                "date='" + date + '\'' +
                ", taskNo='" + taskNo + '\'' +
                ", step=" + step +
                ", distance=" + distance +
                ", calories=" + calories +
                ", duration=" + duration +
                ", avg_step=" + avg_step +
                ", avg_speed=" + avg_speed +
                ", high_speed=" + high_speed +
                ", low_speed=" + low_speed +
                '}';
    }
}
