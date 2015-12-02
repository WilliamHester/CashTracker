package eng.auburn.edu.cashtracker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * Created by william on 12/2/15.
 */
public class CreateTransactionFragment extends Fragment {

    private Account mAccount;
    private Realm mRealm;
    private Transaction mTransaction;

    @Bind(R.id.save) Button mSave;
    @Bind(R.id.transfer) Button mTransfer;
    @Bind(R.id.amount) EditText mValue;
    @Bind(R.id.category_spinner) Spinner mSpinner;
    @Bind(R.id.pos_neg) RadioGroup mPosNeg;

    public static CreateTransactionFragment newInstance(String accountName) {
        Bundle args = new Bundle();
        args.putString("accountName", accountName);
        CreateTransactionFragment fragment = new CreateTransactionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static CreateTransactionFragment newInstance(String accountName, long timeStamp) {
        Bundle args = new Bundle();
        args.putString("accountName", accountName);
        args.putLong("timeStamp", timeStamp);
        CreateTransactionFragment fragment = new CreateTransactionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRealm = Realm.getInstance(getActivity());
        User u = UserManager.getInstance().getUser();
        String accountName = getArguments().getString("accountName");
        if (accountName != null) {
            for (Account a : u.getAccounts()) {
                if (a.getName().equals(accountName)) {
                    mAccount = a;
                    break;
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_transaction, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        final CategoryAdapter adapter = new CategoryAdapter();
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mValue.getText().toString().trim().length() == 0) {
                    return;
                }
                int num = (int) (Double.parseDouble(mValue.getText().toString()) * 100);
                if (mPosNeg.getCheckedRadioButtonId() == R.id.positive) {
                    num = -num;
                }
                int acctTotal = Account.getBalance(mAccount);
                if (acctTotal - num < 0) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.error)
                            .setMessage(R.string.will_go_negative)
                            .setPositiveButton(R.string.okay, null)
                            .show();
                }

                mRealm.beginTransaction();
                Transaction t;
                if (mTransaction == null) {
                    t = mRealm.createObject(Transaction.class);
                    mAccount.getTransactions().add(t);
                    t.setDate(System.currentTimeMillis());
                } else {
                    t = mTransaction;
                }
                t.setAmount(num);
                t.setCategory(mSpinner.getSelectedItemPosition() == adapter.getCount() - 1 ?
                        "Uncategorized" : adapter.getItem(mSpinner.getSelectedItemPosition()));
                mRealm.commitTransaction();

                // TODO: perform check to see if this changed the status of a goal/budget

                getFragmentManager().popBackStack();
            }
        });

        mTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mValue.getText().toString().trim().length() == 0) {
                    return;
                }

                final ArrayList<String> accountNames = new ArrayList<>();
                for (Account a : UserManager.getInstance().getUser().getAccounts()) {
                    if (!mAccount.getName().equals(a.getName())) {
                        accountNames.add(a.getName());
                    }
                }
                final String[] acctNames = new String[accountNames.size()];
                accountNames.toArray(acctNames);

                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.transfer_money_to)
                        .setSingleChoiceItems(acctNames, 0, null)
                        .setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int pos = ((AlertDialog) dialog).getListView()
                                        .getCheckedItemPosition();

                                int num = (int) (Double.parseDouble(mValue.getText().toString()) * 100);
                                if (mPosNeg.getCheckedRadioButtonId() == R.id.positive) {
                                    num = -num;
                                }

                                Account a2 = null;
                                for (Account a : UserManager.getInstance().getUser().getAccounts()) {
                                    if (a.getName().equals(acctNames[pos])) {
                                        a2 = a;
                                        break;
                                    }
                                }

                                mRealm.beginTransaction();
                                Transaction t;
                                if (mTransaction == null) {
                                    t = mRealm.createObject(Transaction.class);
                                    mAccount.getTransactions().add(t);
                                } else {
                                    t = mTransaction;
                                }
                                t.setAmount(num);
                                t.setCategory("Transfer");
                                t.setDate(System.currentTimeMillis());

                                Transaction t2 = mRealm.createObject(Transaction.class);
                                t2.setAmount(-num);
                                t2.setCategory("Transfer");
                                t2.setDate(System.currentTimeMillis() + 1); // lol
                                if (a2 != null) {
                                    a2.getTransactions().add(t2);
                                }
                                mRealm.commitTransaction();

                                getFragmentManager().popBackStack();
                            }
                        })
                        .show();
            }
        });

        if (getArguments().containsKey("timeStamp")) {
            long timeStamp = getArguments().getLong("timeStamp");
            mTransaction = mRealm.where(Transaction.class)
                    .equalTo("date", timeStamp)
                    .findFirst();

            mValue.setText(Utils.getDollarString(mTransaction.getAmount()).substring(1));

            String cat = mTransaction.getCategory();
            int i;
            for (i = 0; i < adapter.getCount() - 1; i++) {
                if (adapter.getItem(i).equals(cat)) {
                    break;
                }
            }
            mSpinner.setSelection(i);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void showErrorDialog(int message) {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.error)
                .setMessage(message)
                .setPositiveButton(R.string.okay, null)
                .show();
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
            return mCategories.contains(s) || s.equalsIgnoreCase("Uncategorized")
                    || s.equalsIgnoreCase("Transfer");
        }
    }
}
