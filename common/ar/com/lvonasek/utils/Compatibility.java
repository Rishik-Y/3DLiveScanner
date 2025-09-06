package com.lvonasek.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;

import com.google.ar.core.ArCoreApk;
import com.google.ar.core.CameraConfig;
import com.google.ar.core.Config;
import com.google.ar.core.Session;

public class Compatibility {

    public static boolean hasToFSensor(Activity activity) {
        try {
            CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null) {
                    if (facing == CameraCharacteristics.LENS_FACING_BACK) {
                        int[] ch = characteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES);
                        if (ch != null) {
                            for (int c : ch) {
                                if (c == CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_DEPTH_OUTPUT) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isARSupported(Context context) {
        ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(context);
        if (availability == ArCoreApk.Availability.SUPPORTED_INSTALLED) {
            return true;
        }
        if (availability == ArCoreApk.Availability.SUPPORTED_APK_TOO_OLD) {
            return true;
        }
        return availability == ArCoreApk.Availability.SUPPORTED_NOT_INSTALLED;
    }

    public static boolean isDaydreamSupported(Context context)
    {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        for (ResolveInfo info : context.getPackageManager().queryIntentActivities( mainIntent, 0))
            if (info.activityInfo.packageName.compareTo("com.google.android.vr.home") == 0)
                return true;
        return false;
    }

    public static boolean isGoogleDepthSupported(Activity activity) {
        try {
            if (!isPlayStoreSupported(activity)) {
                return false;
            }

            Session session = new Session(activity);
            return session.isDepthModeSupported(Config.DepthMode.RAW_DEPTH_ONLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isGoogleToFSupported(Activity activity) {
        try {
            if (!isPlayStoreSupported(activity)) {
                return false;
            }

            Session session = new Session(activity);
            for (CameraConfig config : session.getSupportedCameraConfigs()) {
                if (config.getDepthSensorUsage() == CameraConfig.DepthSensorUsage.REQUIRE_AND_USE) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isPlayStoreSupported(Context context)
    {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        for (ResolveInfo info : context.getPackageManager().queryIntentActivities( mainIntent, 0))
            if (info.activityInfo.packageName.compareTo("com.android.vending") == 0)
                return true;
        return false;
    }
}
