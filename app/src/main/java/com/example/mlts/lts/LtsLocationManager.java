package com.example.mlts.lts;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.mlts.BuildConfig;

// ↓ 以下 import 包名以你下载的 AAR 为准，常见为 net.maiyun.lts.*
 //import net.maiyun.lts.Lts;
 //import net.maiyun.lts.LtsConfig;
 //import net.maiyun.lts.LtsLocation;
 //import net.maiyun.lts.LtsLocationListener;

/**
 * 迈云 LTS 定位
 */
public class LtsLocationManager {

    private static final int REQ_LOCATION = 0x4C54; // "LT"
    private static LtsLocationManager instance;

    private Context appContext;
   // private Lts lts;  // SDK 主对象，集成 AAR 后取消注释

    private LtsLocationCallback singleCallback;
    private LtsLocationCallback continuousCallback;
    private boolean isStarted = false;

    private LtsLocationManager(Context context) {
        this.appContext = context.getApplicationContext();
    }

    public static synchronized LtsLocationManager getInstance(Context context) {
        if (instance == null) {
            instance = new LtsLocationManager(context);
        }
        return instance;
    }

    // ==================== 1. 初始化 ====================

    /**
     * 在 Application.onCreate 中调用（用户同意隐私协议之后）
     */
    public void init() {
        /*if (TextUtils.isEmpty(BuildConfig.LTS_TOKEN)) {
            throw new IllegalStateException("LTS_TOKEN 未配置，请检查 local.properties");
        }*/
       // lts = new Lts(appContext, new LtsConfig(BuildConfig.LTS_TOKEN));
    }

    /**
     * 隐私合规：展示隐私弹窗前调用
     */
    public void onPrivacyShow() {
        // Lts.updatePrivacyShow(appContext, true);
    }

    /**
     * 隐私合规：用户点击同意后调用
     */
    public void onPrivacyAgree() {
        // Lts.updatePrivacyAgree(appContext, true);
        init();
    }

    // ==================== 2. 权限 ====================

    public boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(appContext,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestLocationPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                }, REQ_LOCATION);
    }

    public void requestBackgroundPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                    REQ_LOCATION + 1);
        }
    }

    public boolean onRequestPermissionsResult(int requestCode, int[] grantResults) {
        if (requestCode == REQ_LOCATION && grantResults.length > 0) {
            return grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    // ==================== 3. 单次定位 ====================

    /**
     * 单次融合定位（GPS 弱时自动用基站/Wi-Fi）
     * @param coordType 0=WGS84, 1=BD09, 3=GCJ02（百度地图用 1，国测局用 3）
     */
    public void requestOnce(int coordType, LtsLocationCallback callback) {
        if (!checkReady(callback)) return;

        this.singleCallback = callback;

        // lts.requestLocation(coordType, new LtsLocationListener() {
        //     @Override
        //     public void onLocationChanged(LtsLocation location) {
        //         deliverResult(location, singleCallback);
        //         singleCallback = null;
        //     }
        //     @Override
        //     public void onError(int code, String msg) {
        //         if (singleCallback != null) {
        //             singleCallback.onError(code, msg);
        //             singleCallback = null;
        //         }
        //     }
        // });
    }

    // ==================== 4. 连续定位 ====================

    /**
     * 开始连续定位
     * @param intervalMs 间隔，建议 >= 1000
     */
    public void start(int coordType, int intervalMs, LtsLocationCallback callback) {
        if (!checkReady(callback)) return;

        this.continuousCallback = callback;

        // lts.setLocationInterval(intervalMs);
        // lts.registerLocationListener(new LtsLocationListener() { ... });
        // lts.startLocation();
        isStarted = true;
    }

    public void stop() {
        // if (lts != null) lts.stopLocation();
        isStarted = false;
        continuousCallback = null;
    }

    public boolean isStarted() {
        return isStarted;
    }

    // ==================== 5. 后台定位 ====================

    /**
     * 后台持续定位（需已授权 ACCESS_BACKGROUND_LOCATION + 注册 LtsLocationService）
     */
    public void startBackground(int coordType, int intervalMs, LtsLocationCallback callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                && ContextCompat.checkSelfPermission(appContext,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            callback.onError(-10, "缺少后台定位权限");
            return;
        }
        // lts.startBackgroundLocation(coordType, intervalMs, listener);
        start(coordType, intervalMs, callback);
    }

    public void stopBackground() {
        // lts.stopBackgroundLocation();
        stop();
    }

    // ==================== 内部工具 ====================

    private boolean checkReady(LtsLocationCallback callback) {
        if (!hasLocationPermission()) {
            callback.onError(-1, "未授予定位权限");
            return false;
        }
        return true;
    }

    // private void deliverResult(LtsLocation location, LtsLocationCallback cb) {
    //     if (cb == null || location == null) return;
    //     LtsLocationResult result = new LtsLocationResult();
    //     result.latitude = location.getLatitude();
    //     result.longitude = location.getLongitude();
    //     result.address = location.getAddress();
    //     result.province = location.getProvince();
    //     result.city = location.getCity();
    //     result.district = location.getDistrict();
    //     result.accuracy = location.getAccuracy();
    //     result.timestamp = location.getTime();
    //     cb.onSuccess(result);
    // }
}