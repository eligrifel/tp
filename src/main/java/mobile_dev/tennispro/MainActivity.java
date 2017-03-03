package mobile_dev.tennispro;

import android.app.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.net.URL;
import java.util.Set;
//testing git

public class MainActivity extends AppCompatActivity {
    private String headline ,sub,url;


    private boolean popupSeen;
    private String notificationurl =null;
     private  PopUpWindow popup=null;
private Context context = this;
private WebView webview= null;
    private Activity activity=null;

    public WebView getWebView()
    {
        return webview;
    }
    @Override
    public void onBackPressed() {
    if(webview.canGoBack())webview.goBack();
        else
        super.onBackPressed();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if(savedInstanceState==null)
        {
            popupSeen=true;
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("foreground-notification"));
        /*
        setContentView(R.layout.activity_main);
        */
        getWindow().requestFeature(Window.FEATURE_PROGRESS);

       setContentView(R.layout.activity_main);
        //test preferences values
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

       Log.d( "values",pref.getAll().toString());

               Set<String> test = pref.getStringSet("mode_repeat",null);
        if(test!=null)
        Log.d("values","values from set are "+test.toString());

        //pref.edit().clear().commit();


           // webview = new WebView(this);

        webview=(WebView) findViewById(R.id.webview);


            webview.getSettings().setSaveFormData(true);
            //binding javascript interface
            webview.addJavascriptInterface(new WebAppInterface(this), "android");

           // getWindow().requestFeature(Window.FEATURE_PROGRESS);
           // setContentView(R.layout.activity_main);
            webview.getSettings().setJavaScriptEnabled(true);

             activity = this;
            webview.setWebChromeClient(new WebChromeClient() {

                public void onProgressChanged(WebView view, int progress) {
                    activity.setTitle("טוען..." + progress + "%");
                    if (progress == 100)
                        activity.setTitle("tennispro");
                    activity.setProgress(progress * 100);
                }
            });
            webview.setWebViewClient(new WebViewClient() {
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
                }



                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    Log.d("site","site is "+url);

                    if ((url.equals("http://tennispro.co.il/login-page/"))||url.equals("http://tennispro.co.il/my-account/")) {
                        //getshared pref
                        SharedPreferences sharedPref = context.getSharedPreferences("tennispro", Context.MODE_PRIVATE);
                        String savedusername = sharedPref.getString("username", "");
                        String savedpassword = sharedPref.getString("password", "");


                        webview.loadUrl(
                                "javascript:(function(){function getusername(){document.getElementById('user_login').value = '" + savedusername + "'; }function " +
                                        "getuserpass (){document.getElementById('login_user_pass').value = '" + savedpassword + "';} function submitform(){ document.getElementById('xoouserultra-login-form-1').submit();} getusername();  getuserpass(); })()");
                    }
                }

            });
        //PREFERENCE IS IN DEFAULT SHREDPREFERENCES
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String homepage="http://tennispro.co.il/";
        String result =sharedPref.getString("homepage","");
        Log.d("share","result is "+result);
        switch (result){


            case "1": homepage ="http://tennispro.co.il/";
                break;
            case "2": homepage ="http://tennispro.co.il/rating/";
                break;
            case "3": homepage="http://tennispro.co.il/directory-page/%D7%94%D7%96%D7%9E%D7%A0%D7%94-%D7%9C%D7%9E%D7%A9%D7%97%D7%A7/";
                break;
        }

        String myUrl=null;
        if (getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();

              myUrl= extras.getString("url");
            if(myUrl!=null)
            webview.loadUrl(myUrl);
            else
            webview.loadUrl(homepage);
        }
        else
        {
            if(savedInstanceState==null) {
                webview.loadUrl(homepage);
            }

        }





    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.barmenu, menu);
        return true;
    }

    public void popup (String url , String headline, String sub){
        notificationurl=url;
         popup = new PopUpWindow(this);

        popup.popWindow();
        popup.setTexts(headline,sub);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webview.saveState(outState);
        outState.putString("notificationurl",notificationurl);
        outState.putString("url",url);
        outState.putString("sub",sub);
        outState.putString("headline",headline);
        outState.putBoolean("popupFlag",popupSeen);



    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        notificationurl=(String)savedInstanceState.get("notificationurl");
        webview.restoreState(savedInstanceState);
        url= (String) savedInstanceState.get("url");
        sub=(String)savedInstanceState.get("sub");
        headline=(String)savedInstanceState.get("headline");
        popupSeen=(boolean)savedInstanceState.get("popupFlag");




    }


    // handler for received Intents for the "my-event" event
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            popupSeen=false;


            url = intent.getStringExtra("message");
           headline = intent.getStringExtra("headline");
             sub = intent.getStringExtra("sub");

            popup(url,headline,sub);

        }
    };

    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);


        super.onPause();
        if(popup!=null)
        {
            popup.dismiss();
        }

    }



    public void dissmissPop(View view) {
        popupSeen=true;
        popup.dismiss();
        webview.loadUrl(notificationurl);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!popupSeen) {
            popup(url, headline, sub);
        }

        if (getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();

           String myUrl= extras.getString("url");
            if((myUrl!=null)&&(!(myUrl.equals("")))) {
                getIntent().removeExtra("url");
                webview.loadUrl(myUrl);

            }
        }
    }


    public void openSetting(MenuItem item) {
        Intent i = new Intent(this, MyPreferencesActivity.class);
        startActivity(i);
    }
}




