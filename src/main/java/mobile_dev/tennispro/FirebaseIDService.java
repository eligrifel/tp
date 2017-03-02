package mobile_dev.tennispro;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;


/**
 * Created by eli on 12/30/2016.
 */

public class FirebaseIDService  extends FirebaseInstanceIdService {
    private static final String TAG = "refreshedtoken";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // TODO: Implement this method to send any registration to your app's servers.

        Context mContext = getApplicationContext();
        sendRegistrationToServer(refreshedToken ,mContext);


    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token ,Context mContext) {
        // Add custom implementation, as needed.

        SharedPreferences sharedPref = mContext.getSharedPreferences("tennispro",Context.MODE_PRIVATE);
        if(sharedPref!=null) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("token", token);
            editor.putInt("isNew", 1);
            editor.commit();
            FirebaseMessaging.getInstance().subscribeToTopic("general");
        }


    }



}
