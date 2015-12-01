package eng.auburn.edu.cashtracker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by william on 11/30/15.
 */
public class AccountFragment extends Fragment {

    private final ArrayList<Transaction> mTransactions = new ArrayList<>();

    private Account mAccount;
    private TransactionAdapter mAdapter;

    @Bind(R.id.list) ListView mListView;
    @Bind(R.id.account_total) TextView mAccountTotal;

    public static AccountFragment newInstance(String accountName) {
        Bundle args = new Bundle();
        args.putString("accountName", accountName);
        AccountFragment fragment = new AccountFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String accountName = getArguments().getString("accountName");
        for (Account a : UserManager.getInstance().getUser().getAccounts()) {
            if (a.getName().equals(accountName)) {
                mAccount = a;
                break;
            }
        }


        if (getActivity() != null && getActivity().getActionBar() != null) {
            getActivity().getActionBar().setTitle(accountName);
        }
        if (mTransactions.size() == 0) {
            mTransactions.addAll(mAccount.getTransactions());
        }
        if (mAdapter == null) {
            mAdapter = new TransactionAdapter(getActivity(), mTransactions);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mListView.setAdapter(mAdapter);
        mAccountTotal.setText(Utils.getDollarString(Account.getBalance(mAccount)));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
