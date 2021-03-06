package org.afrikcode.pesmanager.fragments.timeline;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.afrikcode.pesmanager.R;
import org.afrikcode.pesmanager.Utils;
import org.afrikcode.pesmanager.activities.HomeActivity;
import org.afrikcode.pesmanager.adapter.TimelineAdapter;
import org.afrikcode.pesmanager.base.BaseFragment;
import org.afrikcode.pesmanager.decorator.ItemOffsetDecoration;
import org.afrikcode.pesmanager.impl.TimelineImpl;
import org.afrikcode.pesmanager.listeners.OnitemClickListener;
import org.afrikcode.pesmanager.models.Day;
import org.afrikcode.pesmanager.models.Month;
import org.afrikcode.pesmanager.models.Service;
import org.afrikcode.pesmanager.models.Week;
import org.afrikcode.pesmanager.models.Year;
import org.afrikcode.pesmanager.views.TimeStampView;

import java.util.List;


public class YearFragment extends BaseFragment<TimelineImpl> implements OnitemClickListener<Year>, TimeStampView, SearchView.OnQueryTextListener {

    private TimelineAdapter<Year> mYearAdapter;
    private String serviceID;

    public YearFragment() {
        setTitle("Select Year");
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem search = menu.getItem(0);
        search.setVisible(true);

        HomeActivity activity = (HomeActivity) getContext();
        activity.getSearchView().setQueryHint("Search for a year...");

        activity.getSearchView().setOnQueryTextListener(this);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils utils = new Utils();
        HomeActivity activity = (HomeActivity) getContext();
        setImpl(new TimelineImpl(utils.getBranchID(activity), utils.getBranchName(activity)));
        getImpl().setView(this);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (this.getArguments() != null) {
            Bundle b = this.getArguments();
            serviceID = b.getString("ServiceID");
        }

        //setting a global layout manager
        getRv_list().setLayoutManager(new GridLayoutManager(getContext(), 2));
        ItemOffsetDecoration itemOffsetDecoration = new ItemOffsetDecoration(getContext(), R.dimen.recycler_grid_item_offset);
        getRv_list().addItemDecoration(itemOffsetDecoration);
        getRv_list().setHasFixedSize(true);

        mYearAdapter = new TimelineAdapter<>();
        mYearAdapter.setOnclicklistener(this);

        getFab().setVisibility(View.GONE);

        getSwipeRefresh().setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getImpl().getYears(serviceID);
            }
        });

        getImpl().getYears(serviceID);
    }



    @Override
    public void ongetYears(List<Year> yearList) {
        if (yearList.isEmpty()) {
            showErrorLayout("No year Added yet");
            return;
        }

        hideErrorLayout();

        // create adapter and pass data to it

        mYearAdapter.setItemList(yearList);

        getRv_list().setAdapter(mYearAdapter);
        mYearAdapter.notifyDataSetChanged();

        getInfoText().setText("Select year to view transactions made by current branch or add a new year.");
        getInfoText().setVisibility(View.VISIBLE);
    }


    @Override
    public void onError(String error) {
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void ongetServices(List<Service> serviceList) {

    }

    @Override
    public void showLoadingIndicator() {
        super.showLoadingIndicator();

        getInfoText().setText("Please wait... loading data from database");
        getInfoText().setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoadingIndicator() {
        super.hideLoadingIndicator();
        getInfoText().setVisibility(View.GONE);
    }

    @Override
    public void onClick(Year data) {
        if (data.isActive()){
            if (getFragmentListener() != null) {
                Bundle b = new Bundle();
                b.putString("YearID", data.getId());
                b.putString("ServiceID", serviceID);
                MonthFragment mf = new MonthFragment();
                mf.setArguments(b);

                getFragmentListener().moveToFragment(mf);
            }
        }else{
            Toast.makeText(getContext(), data.getName() + " not activated, Contact Administrator for help", Toast.LENGTH_SHORT).show();
        }
    }

    // ************************* This callbacks won't work here *******************//


    @Override
    public void ongetMonthsinYear(List<Month> monthList) {

    }

    @Override
    public void ongetWeeksinMonth(List<Week> weekList) {

    }

    @Override
    public void ongetDaysinWeek(List<Day> dayList) {

    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        mYearAdapter.getFilter().filter(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mYearAdapter.getFilter().filter(newText);
        return false;
    }
}
