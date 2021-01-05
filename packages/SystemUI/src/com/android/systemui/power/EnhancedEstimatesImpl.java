package com.android.systemui.power;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Settings;
import android.util.KeyValueListParser;
import android.util.Log;

import com.android.settingslib.fuelgauge.Estimate;
import com.android.settingslib.utils.PowerUtil;
import com.android.systemui.power.EnhancedEstimates;

import java.time.Duration;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class EnhancedEstimatesImpl implements EnhancedEstimates {

    private Context mContext;
    private final KeyValueListParser mParser = new KeyValueListParser(',');

    @Inject
    public EnhancedEstimatesImpl(Context context) {
        mContext = context;
    }

    @Override
    public boolean isHybridNotificationEnabled() {
        try {
            if (!mContext.getPackageManager().getPackageInfo("com.google.android.apps.turbo",
                    PackageManager.MATCH_DISABLED_COMPONENTS).applicationInfo.enabled)
                return false;
            updateFlags();
            return mParser.getBoolean("hybrid_enabled", true);
        } catch (PackageManager.NameNotFoundException ex) {
            return false;
        }
    }

    @Override
    public Estimate getEstimate() {
        try {
            Cursor cursor = mContext.getContentResolver().query(new Uri.Builder()
                                .scheme("content")
                                .authority("com.google.android.apps.turbo.estimated_time_remaining")
                                .appendPath("time_remaining")
                                .build(), null, null, null, null);
            if (cursor == null || !cursor.moveToFirst()) {
                if (cursor != null) cursor.close();
                Log.e("EnhancedEstimates", "Cannot get an estimate from Turbo: Cursor is null");
                return new Estimate(-1, false, -1);
            }

            boolean basedOnUsage = true;
            if (cursor.getColumnIndex("is_based_on_usage") != -1) {
                if (cursor.getInt(cursor.getColumnIndex("is_based_on_usage")) == 0) {
                    basedOnUsage = false;
                }
            }

            int columnIndex = cursor.getColumnIndex("average_battery_life");
            long avgDischargeTime = -1;
            if (columnIndex != -1) {
                long estimateMillis = cursor.getLong(columnIndex);
                if (estimateMillis != -1) {
                    long millis = Duration.ofMinutes(15).toMillis();
                    if (Duration.ofMillis(estimateMillis).compareTo(Duration.ofDays(1)) >= 0) {
                        millis = Duration.ofHours(1).toMillis();
                    }
                    avgDischargeTime = PowerUtil.roundTimeToNearestThreshold(estimateMillis, millis);
                }
            }
            Estimate estimate = new Estimate(cursor.getLong(
                                             cursor.getColumnIndex("battery_estimate")),
                                             basedOnUsage, avgDischargeTime);
            if (cursor != null) {
                cursor.close();
            }
            return estimate;
        } catch (Exception exception) {
            Log.d("EnhancedEstimates", "Something went wrong when getting an estimate from Turbo", exception);
        }
        return new Estimate(-1, false, -1);
    }

    @Override
    public long getLowWarningThreshold() {
        updateFlags();
        return mParser.getLong("low_threshold", Duration.ofHours(3).toMillis());
    }

    @Override
    public long getSevereWarningThreshold() {
        updateFlags();
        return mParser.getLong("severe_threshold", Duration.ofHours(1).toMillis());
    }

    @Override
    public boolean getLowWarningEnabled() {
        updateFlags();
        return mParser.getBoolean("low_warning_enabled", false);
    }

    protected void updateFlags() {
        try {
            mParser.setString(Settings.Global.getString(mContext.getContentResolver(), "hybrid_sysui_battery_warning_flags"));
        } catch (IllegalArgumentException ex) {
            Log.e("EnhancedEstimates", "Bad hybrid sysui warning flags");
        }
    }
}
