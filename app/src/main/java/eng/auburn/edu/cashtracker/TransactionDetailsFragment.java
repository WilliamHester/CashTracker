package eng.auburn.edu.cashtracker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * Created by william on 12/2/15.
 */
public class TransactionDetailsFragment extends Fragment {

    private Transaction mTransaction;

    @Bind(R.id.amount) TextView mAmount;
    @Bind(R.id.date) TextView mDate;
    @Bind(R.id.category) TextView mCategory;

    public static TransactionDetailsFragment newInstance(long timeStamp) {
        Bundle args = new Bundle();
        args.putLong("timeStamp", timeStamp);
        TransactionDetailsFragment fragment = new TransactionDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Realm realm = Realm.getInstance(getActivity());
        mTransaction = realm.where(Transaction.class)
                .equalTo("date", getArguments().getLong("timeStamp"))
                .findFirst();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mAmount.setText(Utils.getDollarString(mTransaction.getAmount()));
        mDate.setText(DateUtils.formatDateTime(getActivity(), mTransaction.getDate(),
                DateUtils.FORMAT_NUMERIC_DATE));
        mCategory.setText(mTransaction.getCategory());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


}
