package eng.auburn.edu.cashtracker;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
            View v = getLayoutInflater(null).inflate(R.layout.view_change_display_name, null,
                    false);
            final EditText username = (EditText) v.findViewById(R.id.display_name);
            username.setText(UserManager.getInstance().getUser().getDisplayName());
            final AlertDialog d = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.change_display_name)
                    .setView(v)
                    .setPositiveButton(R.string.save, null)
                    .setNegativeButton(R.string.cancel, null)
                    .create();

            d.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button b = d.getButton(DialogInterface.BUTTON_POSITIVE);
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (username.getText().toString().length() < 2) {
                                showErrorDialog(R.string.display_name_too_short);
                            } else {
                                User u = UserManager.getInstance().getUser();
                                mRealm.beginTransaction();
                                u.setDisplayName(username.getText().toString());
                                mRealm.commitTransaction();
                                d.dismiss();
                            }
                        }
                    });
                }
            });
            d.show();
        }
        return true;
    }

    private void showErrorDialog(int message) {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.error)
                .setMessage(message)
                .setPositiveButton(R.string.okay, null)
                .show();
    }
}
