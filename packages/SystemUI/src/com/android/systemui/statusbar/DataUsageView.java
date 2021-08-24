package com.android.systemui.statusbar;

import android.content.Context;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.SystemProperties;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.BidiFormatter;
import android.text.format.Formatter;
import android.text.format.Formatter.BytesResult;
import android.util.AttributeSet;
import android.util.Log;
import android.text.TextUtils;
import android.widget.TextView;

import com.android.internal.telephony.IccCardConstants;
import com.android.internal.telephony.TelephonyIntents;
import com.android.settingslib.net.DataUsageController;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.statusbar.policy.NetworkController;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkTemplate;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.List;

public class DataUsageView extends TextView {

    private static boolean shouldUpdateData;
    private static boolean shouldUpdateDataTextView;
    private ConnectivityManager mConnectivityManager;
    private NetworkController mNetworkController;
    private WifiManager mWifiManager;
    private Context mContext;
    private String formattedinfo;
    private int mSimCount = 0;

    private static final String TAG = "DataUsageView";

    public DataUsageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mNetworkController = Dependency.get(NetworkController.class);
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public void updateUsage() {
        shouldUpdateData = true;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (shouldUpdateData) {
            shouldUpdateData = false;
            AsyncTask.execute(this::updateUsageData);
        }
        if (shouldUpdateDataTextView) {
            shouldUpdateDataTextView = false;
            setText(formattedinfo);
        }
    }

    private void updateSimCount() {
        String simState = SystemProperties.get("gsm.sim.state");
        mSimCount = 0;
        try {
            String[] sims = TextUtils.split(simState, ",");
            for (int i = 0; i < sims.length; i++) {
                if (!sims[i].isEmpty()
                        && !sims[i].equalsIgnoreCase(IccCardConstants.INTENT_VALUE_ICC_ABSENT)
                        && !sims[i].equalsIgnoreCase(IccCardConstants.INTENT_VALUE_ICC_NOT_READY)) {
                    mSimCount++;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error to parse sim state");
        }
    }

    private void updateUsageData() {
        boolean showDailyDataUsage = Settings.System.getInt(getContext().getContentResolver(),
                Settings.System.DATA_USAGE_PERIOD, 1) == 0;
        DataUsageController mobileDataController = new DataUsageController(mContext);
        DataUsageController.DataUsageInfo info;
        String prefix;
        if (isWifiConnected()) {
            final NetworkTemplate template;
            final WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
            if (wifiInfo.getHiddenSSID() || wifiInfo.getSSID() == WifiManager.UNKNOWN_SSID) {
                template = NetworkTemplate.buildTemplateWifiWildcard();
            } else {
                template = NetworkTemplate.buildTemplateWifi(wifiInfo.getSSID());
            }
            info = mobileDataController.getDataUsageInfo(template);
            prefix = mContext.getResources().getString(R.string.usage_wifi_prefix);
            formattedinfo = prefix + ": " + formatDataUsage(info.usageLevel) + " "
                + mContext.getResources().getString(R.string.usage_data);
        } else {
           mobileDataController.setSubscriptionId(
                SubscriptionManager.getDefaultDataSubscriptionId());
           info = showDailyDataUsage ? mobileDataController.getDailyDataUsageInfo()
                : mobileDataController.getDataUsageInfo();
           prefix = getSlotCarrierName();
           updateSimCount();
           if (mSimCount != 0) {
            formattedinfo = prefix + ": " + formatDataUsage(info.usageLevel) + " "
                 + mContext.getResources().getString(R.string.usage_data);
           } else {
            formattedinfo = mContext.getResources().getString(R.string.no_sim_card_available);
          }
        }
        shouldUpdateDataTextView = true;
        invalidate();
    }

    private CharSequence formatDataUsage(long byteValue) {
        final BytesResult res = Formatter.formatBytes(mContext.getResources(), byteValue,
                Formatter.FLAG_IEC_UNITS);
        return BidiFormatter.getInstance().unicodeWrap(mContext.getString(
                com.android.internal.R.string.fileSizeSuffix, res.value, res.units));
    }

    private String getSlotCarrierName() {
        CharSequence result = "";
        SubscriptionManager subManager = mContext.getSystemService(SubscriptionManager.class);
        int subId = subManager.getDefaultDataSubscriptionId();
        List<SubscriptionInfo> subInfoList =
                subManager.getActiveSubscriptionInfoList(true);
        if (subInfoList != null) {
            for (SubscriptionInfo subInfo : subInfoList) {
                if (subId == subInfo.getSubscriptionId()) {
                    result = subInfo.getDisplayName();
                    break;
                }
            }
        }
        return result.toString();
    }

    private boolean isWifiConnected() {
        Network network = mConnectivityManager.getActiveNetwork();
        if (network != null) {
            NetworkCapabilities capabilities = mConnectivityManager.getNetworkCapabilities(network);
            return capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
        } else {
            return false;
        }
    }
}