package eng.auburn.edu.cashtracker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by william on 11/30/15.
 */
public class AccountDetailsFragment extends Fragment {

    private final ArrayList<Transaction> mTransactions = new ArrayList<>();

    private Account mAccount;
    private TransactionAdapter mAdapter;

    @Bind(R.id.list) ListView mListView;
    @Bind(R.id.account_total) TextView mAccountTotal;

    public static AccountDetailsFragment newInstance(String accountName) {
        Bundle args = new Bundle();
        args.putString("accountName", accountName);
        AccountDetailsFragment fragment = new AccountDetailsFragment();
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

        getActivity().setTitle(accountName);
        setHasOptionsMenu(true);

        if (mAdapter == null) {
            mAdapter = new TransactionAdapter(getActivity(), mTransactions,
                    getLayoutInflater(null));
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
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Transaction t = mAdapter.getItem(position);
                getFragmentManager().beginTransaction()
                        .replace(R.id.container,
                                TransactionDetailsFragment.newInstance(mAccount.getName(),
                                        t.getDate()),
                                "TransactionDetails")
                        .addToBackStack("asdf")
                        .commit();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_create_new, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create_new:
                getFragmentManager().beginTransaction()
                        .replace(R.id.container,
                                CreateTransactionFragment.newInstance(mAccount.getName()),
                                "CreateTransactionFragment")
                        .addToBackStack("CreateTransactionFragment")
                        .commit();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        mTransactions.clear();
        mTransactions.addAll(mAccount.getTransactions());
        mAdapter.notifyDataSetChanged();
        mAccountTotal.setText(Utils.getDollarString(Account.getBalance(mAccount)));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
