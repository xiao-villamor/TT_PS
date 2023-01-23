package es.udc.psi.tt_ps.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.firebase.geofire.GeoLocation;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import es.udc.psi.tt_ps.R;
import es.udc.psi.tt_ps.core.firebaseConnection;
import es.udc.psi.tt_ps.databinding.ActivityShowActivitiesBinding;
import es.udc.psi.tt_ps.databinding.FragmentSavedActivitiesBinding;
import es.udc.psi.tt_ps.ui.adapter.ListActivitiesAdapter;
import es.udc.psi.tt_ps.ui.view.DetailsActivity;
import es.udc.psi.tt_ps.ui.view.MainActivity;
import es.udc.psi.tt_ps.ui.viewmodel.ActivityListsPres;
import es.udc.psi.tt_ps.ui.viewmodel.ActivityViewModel;
import es.udc.psi.tt_ps.ui.viewmodel.ListActivities;
import es.udc.psi.tt_ps.ui.viewmodel.SavedActivitiesListPres;


public class SavedActivitiesFragment extends Fragment {

    FragmentSavedActivitiesBinding binding;
    RecyclerView recyclerView;
    List<ListActivities> activitiesList;
    SavedActivitiesListPres presenter = new SavedActivitiesListPres();
    ListActivitiesAdapter listActivitiesAdapter;
    String user = firebaseConnection.getUser();
    ActivityViewModel activityViewModel;
    AutoCompleteTextView autoCompleteTextView;
    private String state = "Admin";


    public static SavedActivitiesFragment newInstance(String param1, String param2) {
        SavedActivitiesFragment fragment = new SavedActivitiesFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSavedActivitiesBinding.inflate(inflater, container, false);
        binding.filterButton.setText("Admin", false);
        //detect when the user change the filter
        autoCompleteTextView = binding.filterButton;

        autoCompleteTextView.setThreshold(1);


        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("TAG", "State: " + state + " - " + autoCompleteTextView.getText().toString());
                if (!(autoCompleteTextView.getText().toString().equals(state))) {
                    Log.d("TAG", "tag Changed");
                    state = autoCompleteTextView.getText().toString();
                    try {
                        Log.d("TAG", "tag Changed" + state);
                        presenter.setRecycledDataByRol(state,recyclerView,user);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    listActivitiesAdapter.notifyDataSetChanged();
                }
            }
        });


        try {
            initRecycledView();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        return binding.getRoot();
    }



    public void initRecycledView() throws InterruptedException {
        activitiesList = new ArrayList<>();

        try {
            presenter.setRecycledDataByRol( activitiesList, user,"Admin");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        listActivitiesAdapter= new ListActivitiesAdapter(activitiesList,getActivity().getApplicationContext(), this::moreActivityInfo);
        activityViewModel = new ActivityViewModel(listActivitiesAdapter);
        recyclerView = binding.listRecycledView;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setAdapter(listActivitiesAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && presenter.getMore) {
                    int totalItems = Objects.requireNonNull(recyclerView.getAdapter()).getItemCount();
                    int lastVisibleItem = ((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager())).findLastVisibleItemPosition();

                    if (lastVisibleItem == totalItems - 1 && lastVisibleItem != 0) {
                        try {
                            if(state.equals("Admin")){
                                presenter.updateRecycledDataByRol(recyclerView, user, "Admin");
                            }else{
                                presenter.updateRecycledDataByRol(recyclerView, user, "Assistant");
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        listActivitiesAdapter.notifyDataSetChanged();
                    }

                }
            }
        });
    }

    public void moreActivityInfo(ListActivities listActivities){
        //Metodo para ir a la vista detallada de actividades
        Log.d("TAG", "Mostrar en detalle" );
        Intent intent = new Intent(getActivity().getApplicationContext(), DetailsActivity.class);
        intent.putExtra("events", listActivities);
        intent.putExtra("latitud",listActivities.getLocation().getLatitude());
        intent.putExtra("longitud",listActivities.getLocation().getLongitude());
        startActivity(intent);
    }

    public void dataChangedSaved(String id ,ListActivities listActivities,String mode){
        Log.d("TAG", "dataChanged: " + id);
        if(mode == "del"){
            Log.d("TAG", "dataChanged: " + "del");
            for (int i = 0; i < activitiesList.size(); i++) {
                if(activitiesList.get(i).getActivityId().equals(id)){
                    activitiesList.remove(i);
                    listActivitiesAdapter.notifyItemRemoved(i);
                    listActivitiesAdapter.notifyItemRangeChanged(i, activitiesList.size());
                    break;
                }
            }
        }else if(mode == "edit"){
            //replace item in the list for the new if the id is the same
            for (int i = 0; i < activitiesList.size(); i++) {
                if(activitiesList.get(i).getActivityId().equals(id)){
                    activitiesList.set(i,listActivities);
                    listActivitiesAdapter.notifyDataSetChanged();
                    break;
                }
            }

        }
    }
}