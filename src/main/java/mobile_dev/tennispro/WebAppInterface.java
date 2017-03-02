package mobile_dev.tennispro;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.webkit.JavascriptInterface;

/**
 * Created by eli on 1/2/2017.
 */

public class WebAppInterface {
    Context mContext;

    /** Instantiate the interface and set the context */
    WebAppInterface(Context c) {
        mContext = c;
    }

    /** Show a toast from the web page */
    @JavascriptInterface
    public void showToast(String userName , String password) {
        savetosharedpref(userName,password);

    }
    @JavascriptInterface
    public void sendUserData(String userName , String password1) {
        SharedPreferences sharedPref = mContext.getSharedPreferences("tennispro",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        //take temp credential store it permement on autocomplete login form
        String username = sharedPref.getString("lastUsername",null);
        String password = sharedPref.getString("lastPassword",null);

        editor.commit();
        //store token on server
        String token =sharedPref.getString("token","");
        int condition = sharedPref.getInt("isNew",0);
        if(condition==1)
        {
            new  postToken(mContext).execute(token);

        }
    }
///check if username and password is equal to last login credentioal and store both of them if not
    //save last password
    private void savetosharedpref(String username,String password) {
        SharedPreferences sharedPref = mContext.getSharedPreferences("tennispro",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("lastUsername", username);
        editor.putString("lastPassword", password);

        editor.commit();


    }

}
