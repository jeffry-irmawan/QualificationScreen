package com.jj.qualificationscreen;

import java.io.File;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceActivity;
import android.preference.PreferenceActivity.Header;
import android.util.Base64;
import android.util.Log;
import android.webkit.JsResult;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    LocationManager locationManager;
    Location location;
    TextView txt;
    String countryCode = "ID";
    JSONArray jsArrayRestricted = new JSONArray();
    JSONArray jsArrayAllowed = new JSONArray();
    String test = "[{1},{2}]";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt = (TextView)findViewById(R.id.txt);
        txt.setText("Build Model=" + Build.MODEL +"\nBuild os = " + Build.VERSION.RELEASE + "test develop");
//        String file = Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/ID.txt";
//        File file = new File(Environment.getExternalStorageDirectory(), "ID.txt");
//        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
//        intent.putExtra(Intent.EXTRA_SUBJECT,"abc");
//        intent.putExtra(Intent.EXTRA_TEXT,"def file :" );
//        intent.putExtra(Intent.EXTRA_CC,"ghi");
//        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));
//        intent.setType("text/html");
//        startActivity(Intent.createChooser(intent, "Send mail"));
//        TextView txtRestricted = (TextView)findViewById(R.id.txtRestricted);
//        TextView txtAllowed = (TextView)findViewById(R.id.txtAllowed);
//        TextView txtCountryCode = (TextView)findViewById(R.id.txtCountryCode);
//        jsArrayRestricted.put("id");
//        jsArrayRestricted.put("au");
//        jsArrayRestricted.put("ca");
//
////        jsArrayAllowed.put("in");
////        jsArrayAllowed.put("my");
////        jsArrayAllowed.put("sg");
//
//        txtRestricted.setText("Restricted =" +jsArrayRestricted.toString());
//        txtAllowed.setText("Allowed=" +jsArrayAllowed.toString());
//        txtCountryCode.setText("Country code =" +countryCode);
//
//        txt.setText("isAvailable =" + getItemAvailableToBought(countryCode, jsArrayAllowed, jsArrayRestricted));
    }


    public boolean getItemAvailableToBought(String countryCode, JSONArray allowedCountriesArr, JSONArray restrictedCountriesArr) {
        boolean isAvailable;
        if(isEmptyRestricted(restrictedCountriesArr)){
            isAvailable = isAllowedCountries(allowedCountriesArr, countryCode);
        }
        else{ 
            if(countryCode.equals("")){
                isAvailable = false;
            }
            else if(isRestrictedCountries(restrictedCountriesArr, countryCode) ){
                isAvailable = false;
            } else if(isAllowedCountries(allowedCountriesArr, countryCode)){
                isAvailable = true;
            }
            else if(!isRestrictedCountries(restrictedCountriesArr, countryCode) && !isAllowedCountries(allowedCountriesArr, countryCode)){
                isAvailable = false;
            }
            else{
                isAvailable = true;
            }
        }
        return isAvailable;
    } 
    public boolean isEmptyRestricted(JSONArray jsRestricted){
        return jsRestricted.length() == 0 ? true : false;
    }

    public boolean isEmptyAllowed(JSONArray jsAllowed){
        return jsAllowed.length() == 0 ? true : false;
    }

    public  boolean isRestrictedCountries(JSONArray jsRestricted, String countryCode){
        boolean isRestricted = false;
        if(isEmptyRestricted(jsRestricted)){
            isRestricted = false;
        } else{
            try {
                for(int i = 0; i < jsRestricted.length() ; i ++){
                    if(jsRestricted.get(i).toString().equalsIgnoreCase(countryCode)){
                        isRestricted = true;
                        break;
                    }
                }
            } catch (JSONException e) {
                isRestricted = true;
                e.printStackTrace();
            }

        }
        return isRestricted;
    }

    public boolean isAllowedCountries(JSONArray jsAllowed, String countryCode){
        boolean isAllowed = false;
        if(isEmptyAllowed(jsAllowed)){
            isAllowed = true;
        }
        else{
            try{
                for(int i = 0; i < jsAllowed.length() ; i ++){
                    if(jsAllowed.get(i).toString().equalsIgnoreCase(countryCode)){
                        isAllowed = true;
                        break;
                    }
                }

            }catch (JSONException e){
                isAllowed = true;
                e.toString();
            }

        }
        return isAllowed;
    }

    public boolean isAllowedRestricted(JSONArray jsAllowed, JSONArray jsRestricted){
        boolean isAllowed = true;
        if(isEmptyRestricted(jsRestricted) || isEmptyAllowed(jsAllowed))
            isAllowed = true;
        else{
            try{
                for (int i = 0 ; i < jsRestricted.length() ; i ++){
                    String restrictedCountry = jsRestricted.get(i).toString();
                    for(int j = 0 ; j < jsAllowed.length() ; j ++){
                        String allowedCountry = jsAllowed.get(j).toString();
                        if(restrictedCountry.equals(allowedCountry))
                            isAllowed = false;
                    }
                }
            } catch(JSONException e){
                e.toString();
            }
        }

        return isAllowed;
    }






    public static String decryptAes256Text(String encryptText, String iv, String secretKey){
        String decryptedText = "";
        byte[] decryptedData = null;
        byte[] key = secretKey.getBytes();
        byte[] ivByte = iv.getBytes();
        Log.d("iv byte length", "ivByteLength=" + ivByte.length);
        Log.d("shared key length", "shared key length=" + key.length);
        SecretKeySpec secretkey = new SecretKeySpec(key, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivByte);
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, secretkey,ivSpec);
            decryptedData = cipher.doFinal(Base64.decode(encryptText, Base64.DEFAULT));

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        String decryptedString = new String(decryptedData);

        return decryptedString;
    }

    //    public  String getDownloadSignature(int loop, int itemId, String timeStamp, int userId){
    //        long timestampLong = Long.parseLong(timeStamp);
    //        String userTimestamp = String.valueOf((long)userId+ timestampLong);
    //        String formatData = timeStamp + "" +(itemId * 2) + "" + userTimestamp + COR_DOWNLOAD_SALT;
    //        String base64String= Base64.encodeToString(formatData.getBytes(), Base64.DEFAULT);
    //        Log.d("timestamp", "timestamp=" +timeStamp);
    //        Log.d("item id x2", "item id x2=" + itemId*2);
    //        Log.d("user id + timestamp", userTimestamp);
    //        Log.d("download salt", COR_DOWNLOAD_SALT);
    //        Log.d("base 64", base64String);
    //        Log.d("format data", "format data=" + formatData);
    //        String signature = "";
    //        for (int i = 0 ; i < loop ; i ++){ 
    //          
    //            if(i == 0){
    //                signature =sha256(COR_DOWNLOAD_SALT.concat(formatData));
    //                Log.d("key " + i, signature);
    //            }
    //            else{
    //                signature = sha256(COR_DOWNLOAD_SALT.concat(signature));
    //                Log.d("key" + i, signature);
    //            }
    //        }
    //        return signature;
    //    }

    public String getIpAddress() {
        String ip ="";
        String str;
        String strHeader = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet("http://www.google.com");
            // HttpGet httpget = new HttpGet("http://whatismyip.com.au/");
            // HttpGet httpget = new HttpGet("http://www.whatismyip.org/");
            HttpResponse response;

            response = httpclient.execute(httpget);
            org.apache.http.Header[] header=httpget.getAllHeaders();
            strHeader = header.toString();
            Log.d("header", strHeader);
            //Log.i("externalip",response.getStatusLine().toString());

            HttpEntity entity = response.getEntity();
            entity.getContentLength();
            str = EntityUtils.toString(entity);
            Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();
            JSONObject json_data = new JSONObject(str);
            ip = json_data.getString("ip");
            Toast.makeText(getApplicationContext(), ip, Toast.LENGTH_LONG).show();
        }
        catch (Exception e){}

        return strHeader;
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
