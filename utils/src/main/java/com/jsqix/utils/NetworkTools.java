package com.jsqix.utils;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import java.util.List;

public class NetworkTools {


	/**
	 * 判断网络状态
	 * context 上下文
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		//获得网络状态管理器
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(connectivityManager == null){
			return false;
		}else{
			NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
			if(info != null){
				for(NetworkInfo network : info){
					if(network.getState() == NetworkInfo.State.CONNECTED){
						return true;
					}
				}
			}
		}
		return false;
	}
	/**  
	 * Gps是否打开  
	 *   
	 * @param context  
	 * @return  
	 */   
	public static boolean isGpsEnabled(Context context) {   
		LocationManager locationManager = ((LocationManager) context   
				.getSystemService(Context.LOCATION_SERVICE));   
		List<String> accessibleProviders = locationManager.getProviders(true);   
		return accessibleProviders != null && accessibleProviders.size() > 0;   
	}   

	/**  
	 * wifi是否打开  
	 */   
	public static boolean isWifiEnabled(Context context) {   
		ConnectivityManager mgrConn = (ConnectivityManager) context   
		.getSystemService(Context.CONNECTIVITY_SERVICE);   
		TelephonyManager mgrTel = (TelephonyManager) context   
		.getSystemService(Context.TELEPHONY_SERVICE);   
		return ((mgrConn.getActiveNetworkInfo() != null && mgrConn   
				.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || mgrTel  
				.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);   
	}   

	/**  
	 * 判断当前网络是否是wifi网络  
	 *   
	 * @param context  
	 * @return boolean  
	 */   
	public static boolean isWifi(Context context) {   
		ConnectivityManager connectivityManager = (ConnectivityManager) context  
		.getSystemService(Context.CONNECTIVITY_SERVICE);   
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();  
		if (activeNetInfo != null   
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {   
			return true;   
		}   
		return false;   
	}   

	/**  
	 * 判断当前网络是否是3G网络  
	 *   
	 * @param context  
	 * @return boolean  
	 */   
	public static boolean isGps(Context context) {   
		ConnectivityManager connectivityManager = (ConnectivityManager) context  
		.getSystemService(Context.CONNECTIVITY_SERVICE);   
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();  
		if (activeNetInfo != null   
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {  
			return true;   
		}   
		return false;   
	}   
}  
