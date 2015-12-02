package eng.auburn.edu.cashtracker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * Created by william on 11/30/15.
 */
public class AccountDetailsFragment extends Fragment {

    private final ArrayList<Transaction> mTransactions = new ArrayList<>();

    private Account mAccount;
    private TransactionAdapter mAdapter;
    private Realm mRealm;

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

        mRealm = Realm.getInstance(getActivity());

        String accountName = getArguments().getString("accountName");
        for (Account a : UserManager.getInstance().getUser().getAccounts()) {
            if (a.getName().equals(accountName)) {
                mAccount = a;
                break;
            }
        }

        getActivity().setTitle(accountName);
        setHasOptionsMenu(true);

        if (mTransactions.size() == 0) {
            mTransactions.addAll(mAccount.getTransactions());
        }
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
                                TransactionDetailsFragment.newInstance(t.getDate()),
                                "TransactionDetails")
                        .addToBackStack("asdf")
                        .commit();
            }
        });
        mAccountTotal.setText(Utils.getDollarString(Account.getBalance(mAccount)));
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
                showCreateTransactionDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showCreateTransactionDialog() {
        View v = getLayoutInflater(null).inflate(R.layout.view_create_transaction, null, false);
        final Spinner s = (Spinner) v.findViewById(R.id.category_spinner);
        final CategoryAdapter adapter = new CategoryAdapter();
        s.setAdapter(adapter);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == adapter.getCount() - 1) {
                    showCreateCategoryDialog();
                }
            }

            private void showCreateCategoryDialog() {
                View newCat = getLayoutInflater(null).inflate(R.layout.view_new_category, null,
                        false);
                final EditText category = (EditText) newCat.findViewById(R.id.category_name);
                final AlertDialog d = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.new_category)
                        .setView(newCat)
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
                                if (adapter.contains(category.getText().toString().trim())) {
                                    showErrorDialog(R.string.category_already_exists);
                                } else {
                                    adapter.add(category.getText().toString().trim());
                                    d.dismiss();
                                }
                            }
                        });
                    }
                });
                d.show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if (adapter.getCount() == 1) {
                    showCreateCategoryDialog();
                }
            }
        });

        final EditText value = (EditText) v.findViewById(R.id.amount);

        final AlertDialog d = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.create_transaction)
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
                        int num = (int) (Double.parseDouble(value.getText().toString()) * 100);
                        int acctTotal = Account.getBalance(mAccount);
                        if (acctTotal - num < 0) {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle(R.string.error)
                                    .setMessage(R.string.will_go_negative)
                                    .setPositiveButton(R.string.okay, null)
                                    .show();
                        }

                        mRealm.beginTransaction();
                        Transaction t = mRealm.createObject(Transaction.class);
                        t.setAmount(num);
                        t.setCategory(s.getSelectedItemPosition() == adapter.getCount() - 1 ?
                                "Uncategorized" : adapter.getItem(s.getSelectedItemPosition()));
                        t.setDate(System.currentTimeMillis());
                        mAccount.getTransactions().add(t);
                        mRealm.commitTransaction();

                        mTransactions.add(t);
                        mAdapter.notifyDataSetChanged();
                        mAccountTotal.setText(Utils.getDollarString(Account.getBalance(mAccount)));

                        d.dismiss();
                    }
                });
            }
        });
        d.show();
    }

    private void showErrorDialog(int message) {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.error)
                .setMessage(message)
                .setPositiveButton(R.string.okay, null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private class CategoryAdapter extends ArrayAdapter<String> {

        private List<String> mCategories;

        public CategoryAdapter() {
            super(getActivity(), android.R.layout.simple_spinner_dropdown_item, android.R.id.text1);
            notifyDataSetChanged();
        }

        @Override
        public void notifyDataSetChanged() {
            mCategories = new ArrayList<>();

            SharedPreferences prefs = getActivity().getSharedPreferences("prefs",
                    Context.MODE_PRIVATE);
            Set<String> strings = prefs.getStringSet("categories", new TreeSet<String>());
            mCategories.addAll(strings);

            super.notifyDataSetChanged();
        }

        @Override
        public String getItem(int position) {
            if (position == mCategories.size()) {
                return getResources().getString(R.string.new_category);
            } else {
                return mCategories.get(position);
            }
        }

        @Override
        public int getCount() {
            return mCategories.size() + 1;
        }

        public void add(String s) {
            mCategories.add(s);
            SharedPreferences.Editor editor = getActivity().getSharedPreferences("prefs",
                    Context.MODE_PRIVATE).edit();
            editor.putStringSet("categories", new TreeSet<>(mCategories));
            editor.commit();
            notifyDataSetChanged();
        }

        public boolean contains(String s) {
            return mCategories.contains(s) || s.equalsIgnoreCase("Uncategorized");
        }
    }
}
