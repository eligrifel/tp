package mobile_dev.tennispro;

/**
 * Created by eli on 1/14/2017.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import java.lang.reflect.Array;
import java.util.Set;

public class MyPreferencesActivity extends PreferenceActivity {
    Context mcontext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();



    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {

            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            Preference pref = findPreference("mode_repeat");

            pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    //
                    Set <String> oldAlertset;
                    Preference pref = findPreference("mode_repeat");
                    oldAlertset= (Set<String>) pref.getSharedPreferences().getStringSet("mode_repeat",null);
                    Log.d("value","mod repeat old value is "+oldAlertset);

                    Log.d("value","new value is "+newValue.toString());
                    Set<String> alertset = (Set<String>) newValue;

                    //find out what have been change and save to fcm topic changes passing 2 Set old and new values
                    checkForChanges(oldAlertset,alertset);





                    // true to update the state of the Preference with the new value
                    // in case you want to disallow the change return false

                    return true;
                }
            });
        }

        private void checkForChanges(Set<String> oldAlertset, Set<String> alertset) {
                Log.d("arrays",oldAlertset.toString()+alertset.toString());
            String unsubscribe ="";
            String subscribe="";
            //if general alert changed
            if ((!(oldAlertset.contains("1")))&&(alertset.contains("1"))) {
                FirebaseMessaging.getInstance().subscribeToTopic("general");
                subscribe += "כל ההתראות";
            }
            else
            if ((oldAlertset.contains("1"))&&(!alertset.contains("1"))) {
                FirebaseMessaging.getInstance().subscribeToTopic("general");
                unsubscribe += "כל ההתראות";
            }
            //if competetion alert changed
            if ((!oldAlertset.contains("2"))&&(alertset.contains("2"))) {
                FirebaseMessaging.getInstance().subscribeToTopic("competition");
                subscribe += "תחרויות ";
            }
            else
            if ((oldAlertset.contains("2"))&&(!alertset.contains("2"))) {
                FirebaseMessaging.getInstance().subscribeToTopic("3");
                unsubscribe += "תחרויות ";
            }

            //if availableToPlay alert changed
            if ((!oldAlertset.contains("3"))&&(alertset.contains("3"))) {
                FirebaseMessaging.getInstance().subscribeToTopic("availableToPlay");
                subscribe += "שחקן פנוי ";
            }
            else
            if ((oldAlertset.contains("3"))&&(!alertset.contains("3"))) {
                FirebaseMessaging.getInstance().subscribeToTopic("availableToPlay");
                unsubscribe += "שחקן פנוי ";

            }
            Toast toast = Toast.makeText(getActivity(), " עודכנו "+subscribe +" " + unsubscribe, Toast.LENGTH_LONG);
            toast.show();


        }
    }

}