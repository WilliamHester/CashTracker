package eng.auburn.edu.cashtracker;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * Created by william on 11/30/15.
 */
public class CreateUserFragment extends Fragment {

    private Realm mRealm;

    @Bind(R.id.display_name) EditText mDisplayName;
    @Bind(R.id.username) EditText mUsername;
    @Bind(R.id.password) EditText mPassword;
    @Bind(R.id.create) Button mCreate;

    public static CreateUserFragment newInstance() {

        Bundle args = new Bundle();

        CreateUserFragment fragment = new CreateUserFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRealm = Realm.getDefaultInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_user, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String displayName = mDisplayName.getText().toString().trim();
                String username = mUsername.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                ArrayList<String> errors = new ArrayList<>();
                if (displayName.length() == 0) {
                    errors.add(getResources().getString(R.string.display_name_too_short));
                }
                if (username.length() < 2) {
                    errors.add(getResources().getString(R.string.username_too_short));
                }
                if (password.length() < 2) {
                    errors.add(getResources().getString(R.string.password_too_short));
                }

                if (errors.size() > 0) {
                    // invalid lengths
                    showErrors(errors);
                    return;
                }

                if (mRealm.where(User.class).equalTo("username", username).findFirst() != null) {
                    // username already exists
                    errors.add(getResources().getString(R.string.username_already_exists));
                    showErrors(errors);
                    return;
                }

                // Hooray! It's valid!
                mRealm.beginTransaction();
                User u = mRealm.createObject(User.class);
                u.setDisplayName(displayName);
                u.setUsername(username);
                u.setPassword(password);
                mRealm.commitTransaction();

                UserManager manager = UserManager.getInstance();
                manager.setUser(u);

                ((LoginActivity) getActivity()).onLoggedIn();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void showErrors(List<String> errors) {
        StringBuilder message = new StringBuilder();
        for (String s : errors) {
            message.append("• ").append(s).append('\n');
        }
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.error)
                .setMessage(message.toString().trim())
                .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }
}
