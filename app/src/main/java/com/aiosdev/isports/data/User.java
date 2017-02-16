package com.aiosdev.isports.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.math.BigDecimal;

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
    private Float sensitivity;
    private int alerm;   //1-开，0-关，默认闹钟开启后每天都响
    private int alermType; //1-闹铃，0-震动
    private String alermTime; //设定时间，格式 00:00

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

    public Float getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(Float sensitivity) {
        this.sensitivity = sensitivity;
    }

    public int getAlerm() {
        return alerm;
    }

    public void setAlerm(int alerm) {
        this.alerm = alerm;
    }

    public int getAlermType() {
        return alermType;
    }

    public void setAlermType(int alermType) {
        this.alermType = alermType;
    }

    public String getAlermTime() {
        return alermTime;
    }

    public void setAlermTime(String alermTime) {
        this.alermTime = alermTime;
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
                ", alerm=" + alerm +
                ", alermType=" + alermType +
                ", alermTime=" + alermTime +
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
            setSensitivity(mPreferences.getFloat("sensitivity", 0));
            setGrade(mPreferences.getString("grade", ""));
            setTitle(mPreferences.getString("title", ""));
            setStepCount(mPreferences.getInt("step_count_plan", 0));
            setTotalStep(mPreferences.getInt("total_step", 0));
            setTotalDistance(mPreferences.getFloat("total_distance", 0));
            setTotalCalories(mPreferences.getFloat("total_calories", 0));
            setTotalDuration(mPreferences.getInt("total_duration", 0));
            setAvgSpeed(mPreferences.getFloat("avg_speed", 0));
            setAlerm(mPreferences.getInt("alerm", 0));
            setAlermType(mPreferences.getInt("alermType", 0));
            setAlermTime(mPreferences.getString("alermTime", "18:00"));
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
            editor.putFloat("sensitivity", getSensitivity());
            editor.putString("grade", getGrade());
            editor.putString("title", getTitle());
            editor.putInt("step_count_plan", getStepCount()); //步
            editor.putInt("total_step", getTotalStep());          //步
            editor.putFloat("total_distance", getTotalDistance());  //公里

            BigDecimal bCalories = new BigDecimal(getTotalCalories());
            float calories = bCalories.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
            editor.putFloat("total_calories", calories);  //卡路里

            editor.putInt("total_duration", getTotalDuration());  //分钟
            editor.putFloat("avg_speed", getAvgSpeed());       //米/秒
            editor.putInt("alerm", getAlerm());  //闹钟开关
            editor.putInt("alermType", getAlermType());
            editor.putString("alermTime", getAlermTime());
            editor.commit();

            res = true;
        }

        return res;
    }
}
