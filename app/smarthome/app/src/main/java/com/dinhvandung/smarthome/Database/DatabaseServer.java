package com.dinhvandung.smarthome.Database;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DatabaseServer extends AsyncTask<String,Boolean, Boolean>{

    private int reqCode;

    @Override
    protected Boolean doInBackground(String... strings) {
        Log.e("Buffer Error", "Error converting result " + strings[0].toString());
        try {
            URL url = new URL(strings[0]);
            HttpURLConnection  httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
            reqCode = httpURLConnection.getResponseCode();
            if (reqCode ==200){
                return true;
            }else {
                return false;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
    }
}
