package com.bluesnap.androidapi.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Created by roy.biber on 05/03/2018.
 */

public class BlueSnapLocalBroadcastManager {
    public static final String SUMMARIZED_BILLING_EDIT = "com.bluesnap.intent.SUMMARIZED_BILLING_EDIT";
    public static final String SUMMARIZED_BILLING_CHANGE = "com.bluesnap.intent.SUMMARIZED_BILLING_CHANGE";
    public static final String SUMMARIZED_SHIPPING_EDIT = "com.bluesnap.intent.SUMMARIZED_SHIPPING_EDIT";
    public static final String SUMMARIZED_SHIPPING_CHANGE = "com.bluesnap.intent.SUMMARIZED_SHIPPING_CHANGE";
    public static final String SHIPPING_SWITCH_ACTIVATED = "com.bluesnap.intent.SHIPPING_SWITCH_ACTIVATED";
    public static final String COUNTRY_CHANGE_REQUEST = "com.bluesnap.intent.COUNTRY_CHANGE_REQUEST";
    public static final String COUNTRY_CHANGE_RESPONSE = "com.bluesnap.intent.COUNTRY_CHANGE_RESPONSE";
    public static final String CURRENCY_UPDATED_EVENT = "com.bluesnap.intent.CURRENCY_UPDATED_EVENT";
    public static final String ONE_LINE_CC_EDIT_FINISH = "com.bluesnap.intent.ONE_LINE_CC_EDIT_FINISH";

    /**
     * a LocalBroadcastManager sendMessage with an extra message inside the intent
     * the message title is the event
     *
     * @param context - {@link Context}
     * @param event   - event String
     * @param Msg     - message for intent extra
     * @param tag     -  simple name string of class
     */
    public static void sendMessage(Context context, String event, boolean Msg, String tag) {
        Log.d(tag, event);
        Intent intent = new Intent(event);
        intent.putExtra(event, Msg);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    /**
     * a LocalBroadcastManager sendMessage with an extra message inside the intent
     * the message title is the event
     *
     * @param context - {@link Context}
     * @param event   - event String
     * @param Msg     - message for intent extra
     * @param tag     -  simple name string of class
     */
    public static void sendMessage(Context context, String event, String Msg, String tag) {
        Log.d(tag, event);
        Intent intent = new Intent(event);
        intent.putExtra(event, Msg);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    /**
     * a LocalBroadcastManager sendMessage
     *
     * @param context - {@link Context}
     * @param event   - event String
     * @param tag     -  simple name string of class
     */
    public static void sendMessage(Context context, String event, String tag) {
        Log.d(tag, event);
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(event));
    }

    /**
     * a LocalBroadcastManager registerReceiver
     *
     * @param context           - {@link Context}
     * @param event             - event String
     * @param broadcastReceiver - {@link BroadcastReceiver}
     */
    public static void registerReceiver(Context context, String event, BroadcastReceiver broadcastReceiver) {
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(event));
    }

    /**
     * a LocalBroadcastManager unregisterReceiver
     *
     * @param context           - {@link Context}
     * @param broadcastReceiver - {@link BroadcastReceiver}
     */
    public static void unregisterReceiver(Context context, BroadcastReceiver broadcastReceiver) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver);
    }

}