package com.aiosdev.isports.data;

/**
 * Created by Administrator on 2017/2/3 0003.
 */

public class Task {

    private String date;//日期
    private String taskNo;//任务编号
    private int step;//步数
    private Double distance;//距离
    private Double calories;//热量
    private Double duration;//时长
    private Double avg_step;//平均步幅
    private Double avg_speed;//平均速度
    private Double high_speed;//最高速度
    private Double low_speed;//最低速度

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

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getCalories() {
        return calories;
    }

    public void setCalories(Double calories) {
        this.calories = calories;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public Double getAvg_step() {
        return avg_step;
    }

    public void setAvg_step(Double avg_step) {
        this.avg_step = avg_step;
    }

    public Double getAvg_speed() {
        return avg_speed;
    }

    public void setAvg_speed(Double avg_speed) {
        this.avg_speed = avg_speed;
    }

    public Double getHigh_speed() {
        return high_speed;
    }

    public void setHigh_speed(Double high_speed) {
        this.high_speed = high_speed;
    }

    public Double getLow_speed() {
        return low_speed;
    }

    public void setLow_speed(Double low_speed) {
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
