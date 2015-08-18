package com.example.pipedrive.android.personslist.main;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.pipedrive.android.personslist.R;
import com.example.pipedrive.android.personslist.data.PersonsContract;
import com.example.pipedrive.android.personslist.data.PersonsDbHelper;
import com.example.pipedrive.android.personslist.detail.DetailActivity;


import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


public class MainActivityFragment extends Fragment {
    private MenuItem refreshButton;
    private TextView emptyView;
    private ProgressBar progressBar;
    private CustomCursorAdapter personsAdapter;
    private PersonsDbHelper personsDbHelper;
    private CompositeSubscription compositeSubscription;
    private PersonsDataManager personsDataManager;
    private boolean refreshEnabled;

    public MainActivityFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        personsDbHelper = PersonsDbHelper.getInstance(getActivity());
        personsDataManager = PersonsDataManager.getInstance(getActivity());
        compositeSubscription = new CompositeSubscription();
        compositeSubscription.add(getSubscription());


    }


    private Subscription getSubscription() {
        return personsDataManager.dataObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Cursor>() {
                    @Override
                    public void onCompleted() {
                        if (!refreshEnabled) {
                            setRefreshButton(true);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        progressBar.setVisibility(View.GONE);
                        setEmptyView(R.color.warning, R.string.failed_request);
                        if (!refreshEnabled) {
                            setRefreshButton(true);
                        }
                    }

                    @Override
                    public void onNext(Cursor cursor) {
                        progressBar.setVisibility(View.GONE);
                        if (cursor.getCount() == 0) {
                            setEmptyView(R.color.regular_text_color, R.string.no_data);
                        } else {
                            personsAdapter.changeCursor(cursor);
                        }


                    }
                });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        personsAdapter = new CustomCursorAdapter(getActivity(), null, 0);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        ListView listView = (ListView) rootView.findViewById(R.id.list_view_persons);
        emptyView = (TextView) rootView.findViewById(R.id.empty_list_view);
        listView.setEmptyView(emptyView);
        listView.setAdapter(personsAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor cursor = (Cursor) personsAdapter.getItem(position);
                if (cursor != null) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra(Intent.EXTRA_TEXT, cursor.getString(PersonsContract.ColumnIndexes.PERSON_ID));
                    startActivity(intent);

                }
            }

        });

        return rootView;
    }

    @Override
    public void onDestroy() {
        personsDbHelper.close();
        compositeSubscription.unsubscribe();
        super.onDestroy();

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_menu, menu);
        refreshButton = menu.findItem(R.id.action_refresh);
        if(refreshEnabled){
            setRefreshButton(true);
        }


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            setRefreshButton(false);
            resetUI();
            personsDataManager.invalidateCache();
            compositeSubscription.add(getSubscription());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void setEmptyView(int colorId, int messageId) {
        emptyView.setTextColor(getResources().getColor(colorId));
        emptyView.setText(messageId);

    }

    private void resetUI() {
        progressBar.setVisibility(View.VISIBLE);
        emptyView.setText("");
        personsAdapter.changeCursor(null);
    }

    private void setRefreshButton(boolean enabled) {
        refreshEnabled = enabled;
        refreshButton.setEnabled(enabled);
        refreshButton.setVisible(enabled);
    }


}
