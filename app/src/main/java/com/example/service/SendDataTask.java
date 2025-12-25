package com.example.service;
import android.os.AsyncTask;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;

public class SendDataTask extends AsyncTask<String, Void, Void> {
    private static final String SERVER = "http://YOUR_SERVER_IP:3000";
    @Override
    protected Void doInBackground(String... params) {
        try {
            String json = params[0];
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
        return null;
    }
}
