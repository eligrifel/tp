package mobile_dev.tennispro;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by eli on 1/4/2017.
 */

public class postToken extends AsyncTask<String,Void,Void>{
Context mcontext=null;
    boolean saveAndDelete=false;
    public postToken (Context context){
    mcontext=context;
    }

    @Override
    protected Void doInBackground(String... tokens) {
        Log.d("async","assync task starts here and token is "+tokens[0]);
        SharedPreferences sharedPref = mcontext.getSharedPreferences("tennispro",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String username = sharedPref.getString("username",null);
        String password = sharedPref.getString("password",null);
        String lastPass=sharedPref.getString("lastPassword",null);
        String lastUserName= sharedPref.getString("lastUsername",null);



        //case last saved password is not null put it in previews vars and save new password credential
        String prevusername=null;
        String prevpassword=null;
        if((password!=null)&&(username!=null)&&(!(lastUserName.equals(username))))
        {
            editor.putString("prevUserName", username);
            editor.putString("prevPassword", password);
            editor.putString("username", lastUserName);
            editor.putString("password", lastPass);
            editor.commit();
             prevusername= username;
            prevpassword=password;
            username=lastUserName;
            password=lastPass;
            saveAndDelete=true;

        }
        else {
            editor.putString("username", lastUserName);
            editor.putString("password", lastPass);
            username=lastUserName;
            password=lastPass;
            editor.commit();
        }

        if((username!=null&password!=null))
        {
//post tp server
            try{

                URL url = new URL("http://tennispro.co.il/addtoken");

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("token", tokens[0]);
                postDataParams.put("user_name", username);
                postDataParams.put("password", password);
                if(saveAndDelete)
                {
                    postDataParams.put("deluser",prevusername );
                    postDataParams.put("delpasspassword", prevpassword);
                }


                Log.d("params",postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(
                            new InputStreamReader(
                                    conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {

                        sb.append(line);

                    }

                    in.close();
                    sb.toString();
                    Log.d("return",sb.toString());

                }
                else {
                    new String("false : "+responseCode);
                }
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }





        return null;
    }
    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }
}
