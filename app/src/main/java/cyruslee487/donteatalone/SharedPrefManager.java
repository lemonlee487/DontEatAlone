package cyruslee487.donteatalone;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static final String SHARED_PREF_NAME = "FCMSharedPref";
    private static final String SHARED_PREF_STATUS = "OwnerStatusSharedPref";
    private static final String SHARED_PREF_USERNAME = "UsernameSharedPref";
    private static final String TAG_TOKEN = "tagtoken";
    private static final String TAG_STATUS = "tagstatus";
    private static final String TAG_EMAIL = "tagemail";
    private static final String TAG_USERNAME = "tagusername";

    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    //this method will save the device token to shared preferences
    public boolean saveDeviceToken(String token){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TAG_TOKEN, token);
        editor.apply();
        return true;
    }

    //this method will fetch the device token from shared preferences
    public String getDeviceToken(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return  sharedPreferences.getString(TAG_TOKEN, null);
    }

    public boolean saveOwnerStatus(String status, String email){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_STATUS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TAG_STATUS, status);
        editor.putString(TAG_EMAIL, email);
        editor.apply();
        return true;
    }

    public boolean saveUsername(String username){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USERNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TAG_USERNAME, username);
        editor.apply();
        return true;
    }

    public String getOwnerStatus(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_STATUS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(TAG_STATUS, "Guest");
    }

    public String getOwnerEmail(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_STATUS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(TAG_EMAIL, null);
    }

    public String getUsername(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USERNAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(TAG_USERNAME, "Username");
    }
}
