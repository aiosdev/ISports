package com.aiosdev.isports.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Administrator on 2017/2/3 0003.
 */

public class User {
    private String name;//名字
    private String sex;//性别
    private String grade;//等级
    private String title;//头衔
    private int stepCount;//计划每天步数
    private int weight;//体重（公斤）
    private int totalStep;//累计步数
    private Float totalDistance;//累计距离（公里）
    private Float totalCalories;//累计热量（卡路里）
    private int totalDuration;//累计时长(秒)
    private int avgStep;//平均步幅（厘米）
    private Float avgSpeed;//平均速度
    private int sensitivity;

    private static User instence;

    public User(Context context) {
        if(getData(context)){
            Log.d("User获取数据：", "成功！");
        }else {
            Log.d("User获取数据：", "失败！");
        }
    }

    public static User getInstence(Context context) {
        if (instence == null) {
            instence = new User(context);
        }
        return instence;
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

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getTotalStep() {
        return totalStep;
    }

    public void setTotalStep(int totalStep) {
        this.totalStep = totalStep;
    }

    public Float getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(Float totalDistance) {
        this.totalDistance = totalDistance;
    }

    public Float getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(Float totalCalories) {
        this.totalCalories = totalCalories;
    }

    public int getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(int totalDuration) {
        this.totalDuration = totalDuration;
    }

    public int getAvgStep() {
        return avgStep;
    }

    public void setAvgStep(int avgStep) {
        this.avgStep = avgStep;
    }

    public Float getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(Float avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public int getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(int sensitivity) {
        this.sensitivity = sensitivity;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", grade='" + grade + '\'' +
                ", title='" + title + '\'' +
                ", stepCount=" + stepCount +
                ", weight=" + weight +
                ", totalStep=" + totalStep +
                ", totalDistance=" + totalDistance +
                ", totalCalories=" + totalCalories +
                ", totalDuration=" + totalDuration +
                ", avgStep=" + avgStep +
                ", avgSpeed=" + avgSpeed +
                ", sensitivity=" + sensitivity +
                '}';
    }

    public boolean getData(Context context){
        boolean res = false;
        SharedPreferences mPreferences = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        if(mPreferences != null) {
            setName(mPreferences.getString("name", ""));
            setSex(mPreferences.getString("sex", ""));
            setWeight(mPreferences.getInt("weight", 0));
            setAvgStep(mPreferences.getInt("pace_length", 0));
            setSensitivity(mPreferences.getInt("sensitivity", 0));
            setGrade(mPreferences.getString("grade", ""));
            setTitle(mPreferences.getString("title", ""));
            setStepCount(mPreferences.getInt("step_count_plan", 0));
            setTotalStep(mPreferences.getInt("total_step", 0));
            setTotalDistance(mPreferences.getFloat("total_distance", 0));
            setTotalCalories(mPreferences.getFloat("total_calories", 0));
            setTotalDuration(mPreferences.getInt("total_duration", 0));
            setAvgSpeed(mPreferences.getFloat("avg_speed", 0));
            res = true;
        }
        return res;
    }

    public boolean saveData(Context context){
        boolean res = false;
        SharedPreferences mPreferences = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        if(mPreferences != null) {
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putString("name", getName());
            editor.putString("sex", getSex());
            editor.putInt("weight", getWeight());
            editor.putInt("pace_length", getAvgStep());
            editor.putInt("sensitivity", getSensitivity());
            editor.putString("grade", getGrade());
            editor.putString("title", getTitle());
            editor.putInt("step_count_plan", getStepCount()); //步
            editor.putInt("total_step", getTotalStep());          //步
            editor.putFloat("total_distance", getTotalDistance());  //公里
            editor.putFloat("total_calories", getTotalCalories());  //卡路里
            editor.putInt("total_duration", getTotalDuration());  //分钟
            editor.putFloat("avg_speed", getAvgSpeed());       //米/秒
            editor.commit();

            res = true;
        }

        return res;
    }
}
