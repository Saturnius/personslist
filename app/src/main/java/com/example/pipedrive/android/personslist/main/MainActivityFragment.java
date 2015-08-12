package com.example.pipedrive.android.personslist.main;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
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


public class MainActivityFragment extends Fragment {
    TextView emptyView;
    ListView listView;
    ProgressBar progressBar;
    private CustomCursorAdapter personsAdapter;
    private Cursor cursor;
    private PersonsDbHelper personsDbHelper;

    //loader manager to access custom cursor loader (ContentLoader) which will run in the background
    private LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new ContentLoader(getActivity());
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            //hide progressbar
            progressBar.setVisibility(View.GONE);

            if (data == null) {
                emptyView.setTextColor(getResources().getColor(R.color.warning));
                emptyView.setText(R.string.failed_request);
            } else if (data.getCount() == 0) {
                emptyView.setTextColor(getResources().getColor(R.color.regular_text_color));
                emptyView.setText(R.string.no_data);
            }
            cursor = data;
            personsAdapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            personsAdapter.swapCursor(null);
        }

    };

    public MainActivityFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        personsDbHelper = PersonsDbHelper.getInstance(getActivity());

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        personsAdapter = new CustomCursorAdapter(getActivity(), cursor, 0);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        if(cursor == null){
        progressBar.setVisibility(View.VISIBLE);}
        listView = (ListView) rootView.findViewById(R.id.list_view_persons);
        emptyView = (TextView) rootView.findViewById(R.id.empty_list_view);
        listView.setEmptyView(emptyView);
        listView.setAdapter(personsAdapter);

        //start new activity on click
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
    public void onStart() {
        super.onStart();
        if (cursor == null) {
            loadData();
        }
    }

    public void loadData() {
        getActivity().getSupportLoaderManager().initLoader(R.id.content_loader, null, loaderCallbacks);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        personsDbHelper.deleteAllTables(personsDbHelper.getWritableDatabase());
        cursor.close();
        personsDbHelper.close();

    }

    //menu with refresh button
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_menu, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            getActivity().getSupportLoaderManager().destroyLoader((R.id.content_loader));
            progressBar.setVisibility(View.VISIBLE);
            loadData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
