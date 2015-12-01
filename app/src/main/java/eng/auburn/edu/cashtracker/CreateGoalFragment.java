package eng.auburn.edu.cashtracker;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * Created by william on 12/1/15.
 */
public class CreateGoalFragment extends Fragment {

    private Realm mRealm;

    @Bind(R.id.goal_name) EditText mGoalName;
    @Bind(R.id.goal_amount) EditText mGoalAmount;
    @Bind(R.id.create) Button mCreate;
    @Bind(R.id.accounts_spinner) Spinner mSpinner;

    public static CreateGoalFragment newInstance() {
        Bundle args = new Bundle();
        CreateGoalFragment fragment = new CreateGoalFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRealm = Realm.getInstance(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_goal, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        final AccountsAdapter adapter = new AccountsAdapter();
        mSpinner.setAdapter(adapter);

        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User u = UserManager.getInstance().getUser();
                for (Goal g : u.getGoals()) {
                    if (g.getName().equalsIgnoreCase(mGoalName.getText().toString().trim())) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle(R.string.error)
                                .setMessage(R.string.goal_already_exists)
                                .setPositiveButton(R.string.okay, null)
                                .show();
                        return;
                    }
                }

                String name = mGoalName.getText().toString();
                int target = (int) (Double.parseDouble(mGoalAmount.getText().toString()) * 100);

                Account selected = null;
                for (Account a : UserManager.getInstance().getUser().getAccounts()) {
                    if (a.getName().equals(adapter.getItem(mSpinner.getSelectedItemPosition()))) {
                        selected = a;
                        break;
                    }
                }

                mRealm.beginTransaction();
                Goal g = mRealm.createObject(Goal.class);
                g.setAmount(target);
                g.setName(name);
                g.setAccount(selected);
                u.getGoals().add(g);
                mRealm.commitTransaction();

                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private class AccountsAdapter extends ArrayAdapter<String> {
        public AccountsAdapter() {
            super(getActivity(), android.R.layout.simple_spinner_dropdown_item, android.R.id.text1);

            for (Account a : UserManager.getInstance().getUser().getAccounts()) {
                add(a.getName());
            }
        }
    }
}
