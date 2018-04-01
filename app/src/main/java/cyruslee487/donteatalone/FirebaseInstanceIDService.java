package cyruslee487.donteatalone;


import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class FirebaseInstanceIDService extends FirebaseInstanceIdService{

    private static final String TAG = "DB";
    private static final String FILENAME = "FIREBASE_INSTANCE_ID.txt";
    
    private OutputStreamWriter outputStreamWriter;

    @Override
    public void onTokenRefresh() {
        //get updated InstanceId token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "onTokenRefresh: " + refreshedToken);

        try{
            outputStreamWriter = new OutputStreamWriter(
                    getApplicationContext().openFileOutput(FILENAME, Context.MODE_PRIVATE));
            outputStreamWriter.write(refreshedToken);
            outputStreamWriter.flush();
            outputStreamWriter.close();
            Log.d(TAG, "onTokenRefresh: File written");
        }catch(Exception e){
            Log.e(TAG, "onTokenRefresh: " + e.getMessage());
        }
    }
}
