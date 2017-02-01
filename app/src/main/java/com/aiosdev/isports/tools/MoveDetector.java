package com.aiosdev.isports.tools;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
//走步检测器，用于检测走步并计数

/**
 * 具体算法不太清楚，本算法是从谷歌计步器：Pedometer上截取的部分计步算法
 * 传感器API:三个类，一个接口
 * SensorManager 类------可以通过这个类去创建一个传感器服务的实例，这个类提供的各种方法可以访问传感器列表、注册或解除注册传感器事件监听、获取方位信息等。
 * Sensor 类------用于创建一个特定的传感器实例，这个类提供的方法可以让你决定一个传感器的功能。
 * SensorEvent 类-----系统会通过这个类创建一个传感器事件对象，提供了一个传感器的事件信息，包含一下内容，原生的传感器数据、触发传感器的事件类型、精确的数据以及事件发生的时间。
 * SensorEventListener 接口------可以通过这个接口创建两个回调用法来接收传感器的事件通知，比如当传感器的值发生变化时。
 *
 */
public class MoveDetector implements SensorEventListener {//实现取得感应检测Sensor状态的监听功能

	public static int CURRENT_SETP = 0;

	public static float SENSITIVITY = 0;   //SENSITIVITY灵敏度

	private float mLastValues[] = new float[3 * 2];
	private float mScale[] = new float[2];
	private float mYOffset;
	private static long end = 0;
	private static long start = 0;

	/**
	 * 最后加速度方向
	 */
	private float mLastDirections[] = new float[3 * 2];
	private float mLastExtremes[][] = { new float[3 * 2], new float[3 * 2] };
	private float mLastDiff[] = new float[3 * 2];
	private int mLastMatch = -1;

	/**
	 * 传入上下文的构造函数
	 *
	 * @param context
	 */
	public MoveDetector(Context context) {
		// TODO Auto-generated constructor stub
		super();
		int h = 480;
		mYOffset = h * 0.5f;
		mScale[0] = -(h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
		mScale[1] = -(h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
		/*
		if (SettingsActivity.sharedPreferences == null) {
			SettingsActivity.sharedPreferences = context.getSharedPreferences(
					SettingsActivity.SETP_SHARED_PREFERENCES,
					Context.MODE_PRIVATE);
		}
		SENSITIVITY = SettingsActivity.sharedPreferences.getInt(
				SettingsActivity.SENSITIVITY_VALUE, 3);
	*/}

	// public void setSensitivity(float sensitivity) {
	// SENSITIVITY = sensitivity; // 1.97 2.96 4.44 6.66 10.00 15.00 22.50
	// // 33.75
	// // 50.62
	// }

	// public void onSensorChanged(int sensor, float[] values) {
	@Override
	public void onSensorChanged(SensorEvent event) {//在感应检测到Sensor的值有变化时会被调用到
		// Log.i(Constant.STEP_SERVER, "StepDetector");
		Sensor sensor = event.sensor;
		// Log.i(Constant.STEP_DETECTOR, "onSensorChanged");
		synchronized (this) {
			if (sensor.getType() == Sensor.TYPE_ORIENTATION) {//方位感应检测
			} else {
				int j = (sensor.getType() == Sensor.TYPE_ACCELEROMETER) ? 1 : 0;//加速度感应检测
				if (j == 1) {//加速度感应发生改变，则执行如下操作
					float vSum = 0;
					for (int i = 0; i < 3; i++) {
						/*
						* Accelerometer Sensor测量的是所有施加在设备上的力所产生的加速度的负值（包括重力加速度）。
						* 加速度所使用的单位是m/sec^2，数值是加速度的负值。
						* SensorEvent.values[0]：加速度在X轴的负值
						* SensorEvent.values[1]：加速度在Y轴的负值
						* SensorEvent.values[2]：加速度在Z轴的负值
						* 例如：当手机Z轴朝上平放在桌面上，并且从左到右推动手机，此时X轴上的加速度是正数。
						* 当手机Z轴朝上静止放在桌面上，此时Z轴的加速度是+9.81m/sec^2。
						*
						* */

						final float v = mYOffset + event.values[i] * mScale[j];
						vSum += v;
					}
					int k = 0;
					float v = vSum / 3;

					float direction = (v > mLastValues[k] ? 1: (v < mLastValues[k] ? -1 : 0));
					if (direction == -mLastDirections[k]) {
						// Direction changed
						int extType = (direction > 0 ? 0 : 1); // minumum or
						// maximum?
						mLastExtremes[extType][k] = mLastValues[k];
						float diff = Math.abs(mLastExtremes[extType][k]- mLastExtremes[1 - extType][k]);

						if (diff > SENSITIVITY) {
							boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff[k] * 2 / 3);
							boolean isPreviousLargeEnough = mLastDiff[k] > (diff / 3);
							boolean isNotContra = (mLastMatch != 1 - extType);

							if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough && isNotContra) {
								end = System.currentTimeMillis();
								if (end - start > 500) {// 此时判断为走了一步
									Log.i("StepDetector", "CURRENT_SETP:"
											+ CURRENT_SETP);
									CURRENT_SETP++;
									mLastMatch = extType;
									start = end;
								}
							} else {
								mLastMatch = -1;
							}
						}
						mLastDiff[k] = diff;
					}
					mLastDirections[k] = direction;
					mLastValues[k] = v;
				}
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}

}
