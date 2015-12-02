package eng.auburn.edu.cashtracker;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import io.realm.Realm;

/**
 * Created by william on 12/2/15.
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    private Realm mRealm;

    public static SettingsFragment newInstance() {
        Bundle args = new Bundle();
        SettingsFragment fragment = new SettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
        mRealm = Realm.getInstance(getActivity());
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference == findPreference("pref_delete_user")) {
            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.delete_user)
                    .setMessage(R.string.delete_user_message)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mRealm.beginTransaction();
                            UserManager.getInstance().getUser().removeFromRealm();
                            mRealm.commitTransaction();

                            getActivity().setResult(Activity.RESULT_OK);
                            getActivity().finish();
                        }
                    })
                    .show();
        } else if (preference == findPreference("pref_change_display_name")) {

        }
        return true;
    }
}
