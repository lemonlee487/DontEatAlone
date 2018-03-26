package cyruslee487.donteatalone;


import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FirebaseInstanceIDService extends FirebaseInstanceIdService{

    private static final String TAG = "DB";

    @Override
    public void onTokenRefresh() {
        //get updated InstanceId token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "onTokenRefresh: " + refreshedToken);
        Log.e(TAG, "onTokenRefresh: " + refreshedToken);
    }
}
