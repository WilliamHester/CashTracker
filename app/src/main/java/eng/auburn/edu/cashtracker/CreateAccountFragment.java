package eng.auburn.edu.cashtracker;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * Created by william on 11/30/15.
 */
public class CreateAccountFragment extends Fragment {

    private Realm mRealm;

    @Bind(R.id.account_name) EditText mAccountName;
    @Bind(R.id.balance) EditText mBalance;
    @Bind(R.id.interest_rate) EditText mInterestRate;
    @Bind(R.id.interest_frequency) Spinner mFrequency;
    @Bind(R.id.create) Button mCreate;

    public static CreateAccountFragment newInstance() {
        Bundle args = new Bundle();
        CreateAccountFragment fragment = new CreateAccountFragment();
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
        return inflater.inflate(R.layout.fragment_create_account, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User u = UserManager.getInstance().getUser();
                for (Account a : u.getAccounts()) {
                    if (a.getName().equalsIgnoreCase(mAccountName.getText().toString().trim())) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle(R.string.error)
                                .setMessage(R.string.account_already_exists)
                                .setPositiveButton(R.string.okay, null)
                                .show();
                        return;
                    }
                }

                int balance = (int) (Double.parseDouble(mBalance.getText().toString().trim())
                        * 100);
                double interestRate = Double.parseDouble(mInterestRate.getText().toString().trim());
                String frequency = (String) mFrequency.getSelectedItem();

                mRealm.beginTransaction();
                Account a = mRealm.createObject(Account.class);
                a.setInterestRate(interestRate);
                a.setInterestRateFrequency(frequency);
                a.setStartingBalance(balance);
                a.setName(mAccountName.getText().toString().trim());
                u.getAccounts().add(a);
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
}
