package mobile_dev.tennispro;

import android.annotation.TargetApi;
import android.app.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
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
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
//testing git

public class MainActivity extends AppCompatActivity {


    public static final int INPUT_FILE_REQUEST_CODE = 1;
    public static final String EXTRA_FROM_NOTIFICATION = "EXTRA_FROM_NOTIFICATION";

    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;

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
                //start
                public boolean onShowFileChooser(
                        WebView webView, ValueCallback<Uri[]> filePathCallback,
                        WebChromeClient.FileChooserParams fileChooserParams) {
                    if(mFilePathCallback != null) {
                        mFilePathCallback.onReceiveValue(null);
                    }
                    mFilePathCallback = filePathCallback;

                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
                        // Create the File where the photo should go
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                            takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                        } catch (IOException ex) {
                            // Error occurred while creating the File

                        }

                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                    Uri.fromFile(photoFile));
                        } else {
                            takePictureIntent = null;
                        }
                    }

                    Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                    contentSelectionIntent.setType("image/*");

                    Intent[] intentArray;
                    if(takePictureIntent != null) {
                        intentArray = new Intent[]{takePictureIntent};
                    } else {
                        intentArray = new Intent[0];
                    }

                    Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                    chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                    chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

                    startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);

                    return true;
                }





/// end
                public void onProgressChanged(WebView view, int progress) {
                    activity.setTitle("טוען..." + progress + "%");
                    if (progress == 100)
                        activity.setTitle("tennispro");
                    activity.setProgress(progress * 100);
                }
            });

        if(webview.getUrl() == null) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            String result =sharedPref.getString("homepage","");

            webview.loadUrl(result);
        }





            webview.setWebViewClient(new WebViewClient() {

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    // open in Webview
                    if (url.contains(".co.il") ){

                        return false;
                    }
                    // open rest of URLS in default browser
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }

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
        webview.onPause();
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
             //   webview.loadUrl(myUrl);

            }
        }
        webview.onResume();
    }


    public void openSetting(MenuItem item) {
        Intent i = new Intent(this, MyPreferencesActivity.class);
        startActivity(i);
    }

    /**
     * More info this method can be found at
     * http://developer.android.com/training/camera/photobasics.html
     *
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return imageFile;
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setUpWebViewDefaults(WebView webView) {
        WebSettings settings = webView.getSettings();

        // Enable Javascript
        settings.setJavaScriptEnabled(true);

        // Use WideViewport and Zoom out if there is no viewport defined
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        // Enable pinch to zoom without the zoom buttons
        settings.setBuiltInZoomControls(true);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            // Hide the zoom controls for HONEYCOMB+
            settings.setDisplayZoomControls(false);
        }

        // Enable remote debugging via chrome://inspect
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        // We set the WebViewClient to ensure links are consumed by the WebView rather
        // than passed to a browser if it can
        webView.setWebViewClient(new WebViewClient());
    }
    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if(requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        Uri[] results = null;

        // Check that the response is a good one
        if(resultCode == Activity.RESULT_OK) {
            if(data == null) {
                // If there is not data, then we may have taken a photo
                if(mCameraPhotoPath != null) {
                    results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                }
            } else {
                String dataString = data.getDataString();
                if (dataString != null) {
                    results = new Uri[]{Uri.parse(dataString)};
                }
            }
        }

        mFilePathCallback.onReceiveValue(results);
        mFilePathCallback = null;
        return;
    }

}




