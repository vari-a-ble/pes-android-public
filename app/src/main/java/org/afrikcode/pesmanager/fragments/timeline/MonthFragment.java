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

import butterknife.BindArray;

public class MonthFragment extends BaseFragment<TimelineImpl> implements OnitemClickListener<Month>, TimeStampView, SearchView.OnQueryTextListener {

    @BindArray(R.array.months_array) String[] months;
    private String serviceID, yearID;
    private TimelineAdapter<Month> monthAdapter;

    public MonthFragment() {
        setTitle("Select Month");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem search = menu.getItem(0);
        search.setVisible(true);

        HomeActivity activity = (HomeActivity) getContext();
        activity.getSearchView().setQueryHint("Search for a month...");

        activity.getSearchView().setOnQueryTextListener(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils utils = new Utils();
        HomeActivity homeActivity = (HomeActivity) getContext();

        setImpl(new TimelineImpl(utils.getBranchID(homeActivity), utils.getBranchName(homeActivity)));
        getImpl().setView(this);
        setHasOptionsMenu(true);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (this.getArguments() != null) {
            Bundle b = this.getArguments();
            yearID = b.getString("YearID");
            serviceID = b.getString("ServiceID");
        }

        //setting a global layout manager
        getRv_list().setLayoutManager(new GridLayoutManager(getContext(), 2));
        ItemOffsetDecoration itemOffsetDecoration = new ItemOffsetDecoration(getContext(), R.dimen.recycler_grid_item_offset);
        getRv_list().addItemDecoration(itemOffsetDecoration);

        monthAdapter = new TimelineAdapter<>();
        monthAdapter.setOnclicklistener(this);

        getFab().setVisibility(View.GONE);

        getSwipeRefresh().setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getImpl().getMonthsinYear(yearID);
            }
        });

        getImpl().getMonthsinYear(yearID);
    }

    @Override
    public void ongetMonthsinYear(List<Month> monthList) {
        if (monthList.isEmpty()) {
            showErrorLayout("No month Added yet");
            return;
        }

        hideErrorLayout();

        // create adapter and pass data to it
        monthAdapter.setItemList(monthList);

        getRv_list().setAdapter(monthAdapter);
        monthAdapter.notifyDataSetChanged();

        getInfoText().setText("Select month to view transactions made by current branch or add a new month.");
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
        getInfoText().setText("Please wait... loading months from database");
        getInfoText().setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoadingIndicator() {
        super.hideLoadingIndicator();
        getInfoText().setVisibility(View.GONE);
    }

    @Override
    public void onClick(Month data) {
        if (data.isActive()){
            if (getFragmentListener() != null) {
                Bundle b = new Bundle();
                b.putString("YearID", yearID);
                b.putString("MonthID", data.getId());
                b.putString("ServiceID", serviceID);
                WeekFragment wf = new WeekFragment();
                wf.setArguments(b);

                getFragmentListener().moveToFragment(wf);
            }
        }else {
            Toast.makeText(getContext(), data.getName() + " not activated, Contact Administrator for help", Toast.LENGTH_SHORT).show();
        }
    }

    //********************************** This callbacks won't work here *********************//


    @Override
    public void ongetYears(List<Year> yearList) {

    }

    @Override
    public void ongetWeeksinMonth(List<Week> weekList) {

    }

    @Override
    public void ongetDaysinWeek(List<Day> dayList) {

    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        monthAdapter.getFilter().filter(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        monthAdapter.getFilter().filter(newText);
        return false;
    }
}
