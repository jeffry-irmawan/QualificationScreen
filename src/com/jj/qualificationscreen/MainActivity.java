package com.jj.qualificationscreen;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends Activity {
    LocationManager locationManager;
    Location location;
    TextView txt;
    String COR_DOWNLOAD_SALT = "36yjfhjgy86o75i6urkyfhjgvbcftdfgnbccnhu756yjthmfgjgk876uky76iyjtd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String date = "2014-10-13 13:16:03";
        Calendar calendar = Calendar.getInstance();
        txt = (TextView)findViewById(R.id.txt);
        txt.setText(getDownloadSignature(12, 27335, "192461648711", 81285));
    }
    
    public  String getDownloadSignature(int loop, int itemId, String timeStamp, int userId){
        long timestampLong = Long.parseLong(timeStamp);
        String userTimestamp = String.valueOf((long)userId+ timestampLong);
        String formatData = timeStamp + "" +(itemId * 2) + "" + userTimestamp + COR_DOWNLOAD_SALT;
        String base64String= Base64.encodeToString(formatData.getBytes(), Base64.DEFAULT);
        Log.d("timestamp", "timestamp=" +timeStamp);
        Log.d("item id x2", "item id x2=" + itemId*2);
        Log.d("user id + timestamp", userTimestamp);
        Log.d("download salt", COR_DOWNLOAD_SALT);
        Log.d("base 64", base64String);
        Log.d("format data", "format data=" + formatData);
        String signature = "";
        for (int i = 0 ; i < loop ; i ++){ 
          
            if(i == 0){
                signature =sha256(COR_DOWNLOAD_SALT.concat(formatData));
                Log.d("key " + i, signature);
            }
            else{
                signature = sha256(COR_DOWNLOAD_SALT.concat(signature));
                Log.d("key" + i, signature);
            }
        }
        return signature;
    }
    
    public  String sha256(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

}
