package com.aiosdev.isports.data;

/**
 * Created by Administrator on 2017/2/3 0003.
 */

public class User {
    private String name;//名字
    private String sex;//性别
    private String grade;//等级
    private String title;//头衔
    private int stepCount;//计划每天步数
    private Double weight;//体重
    private int totalStep;//累计步数
    private Double totalDistance;//累计距离
    private Double totalCalories;//累计热量
    private Double totalDuration;//累计时长
    private Double avgStep;//平均步幅
    private Double avgSpeed;//平均速度

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getStepCount() {
        return stepCount;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public int getTotalStep() {
        return totalStep;
    }

    public void setTotalStep(int totalStep) {
        this.totalStep = totalStep;
    }

    public Double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(Double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public Double getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(Double totalCalories) {
        this.totalCalories = totalCalories;
    }

    public Double getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(Double totalDuration) {
        this.totalDuration = totalDuration;
    }

    public Double getAvgStep() {
        return avgStep;
    }

    public void setAvgStep(Double avgStep) {
        this.avgStep = avgStep;
    }

    public Double getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(Double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", grade='" + grade + '\'' +
                ", title='" + title + '\'' +
                ", stepCount='" + stepCount + '\'' +
                ", weight='" + weight + '\'' +
                ", totalStep='" + totalStep + '\'' +
                ", totalDistance='" + totalDistance + '\'' +
                ", totalCalories='" + totalCalories + '\'' +
                ", totalDuration='" + totalDuration + '\'' +
                ", avgStep='" + avgStep + '\'' +
                ", avgSpeed='" + avgSpeed + '\'' +
                '}';
    }
}
