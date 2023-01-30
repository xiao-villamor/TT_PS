package es.udc.psi.tt_ps.ui.fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import es.udc.psi.tt_ps.R;
import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.databinding.ActivityShowActivitiesBinding;
import es.udc.psi.tt_ps.ui.view.ActivityCreateActivity;
import es.udc.psi.tt_ps.ui.view.DetailsActivity;
import es.udc.psi.tt_ps.ui.viewmodel.ActivityListsPres;
import es.udc.psi.tt_ps.ui.viewmodel.ActivityViewModel;
import es.udc.psi.tt_ps.ui.viewmodel.ListActivities;
import es.udc.psi.tt_ps.ui.adapter.ListActivitiesAdapter;

public class ActivityListFragment extends Fragment {
    String TAG = "_TAG";
    String ACTIVITY = "MainActivity2";
    List<ListActivities> activitiesList;
    ActivityListsPres presenter = new ActivityListsPres();
    RecyclerView recyclerView;
    ActivityShowActivitiesBinding binding;
    ListActivitiesAdapter listActivitiesAdapter;
    List<String> tags = new ArrayList<>();
    List<Float> range = new ArrayList<>();
    FusedLocationProviderClient mFusedLocationClient;
    LocationManager mLocationManager;
    GeoLocation mLocation;
    private FragmentListener listener;
    static boolean getMore = true;
    Context ctx;
    ActivityViewModel activityViewModel;

    public static ActivityListFragment newInstance() {
        return new ActivityListFragment();
    }

    public interface FragmentListener {
        void onFragmentInteraction(List<String> tags, List<Float> range, GeoLocation location,
                                   ListActivitiesAdapter listActivitiesAdapter, ActivityListsPres presenter,
                                   RecyclerView recyclerView);
    }



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = ActivityShowActivitiesBinding.inflate(inflater, container, false);

        tags = Arrays.asList(getResources().getStringArray(R.array.interests_array));
        range = Arrays.asList(0f, 60f);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity().getApplicationContext());

        try {
            initRecycledView();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        binding.floatingActionButtonAdd.setOnClickListener(v -> {
            Intent intentSend = new Intent(getActivity().getApplicationContext(), ActivityCreateActivity.class);
            startActivity(intentSend);
        });
        binding.filterButton.setOnClickListener(v -> {
            listener.onFragmentInteraction( tags, range, mLocation, listActivitiesAdapter,presenter,recyclerView);

        });
        return binding.getRoot();
    }

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentListener) {
            listener = (FragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentListener");
        }
    }

    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private void getLocation() throws InterruptedException {
        mLocationManager = (LocationManager)getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);


        Result<GeoLocation, Exception> res = new Result<>();

            Thread thread = new Thread(() -> {
                Looper.prepare();
                try {
                    Location bestLocation = null;
                    if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    for (String provider : providers) {
                        Location l = mLocationManager.getLastKnownLocation(provider);
                        if (l == null) {
                            continue;
                        }
                        if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                            bestLocation = l;
                        }

                    }
                    if(bestLocation == null){
                        bestLocation = new Location("noProvider");
                        bestLocation.setLatitude(-79.159623);
                        bestLocation.setLongitude(24.548291);
                    }
                    mLocation = new GeoLocation(bestLocation.getLatitude(), bestLocation.getLongitude());
                } catch (Exception e) {
                    Log.d(TAG, "getLocation exception: " + e.getMessage());
                    res.exception = e;
                }
            });
            thread.start();
            thread.join();
            thread.interrupt();

    }

    public void updateList(List<String> aTags, List<Float> aRange){
        Log.d(TAG, "Cambios filtros");
        getMore = true;

        range = aRange;
        tags = aTags;
    }

    public void initRecycledView() throws InterruptedException {
        activitiesList = new ArrayList<>();
        getLocation();
        try {
            presenter.setRecycledDataFiltered(tags, activitiesList,range ,mLocation);
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
                            presenter.updateRecycledDataFiltered(tags,recyclerView,range ,mLocation);
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

    public void dataChanged(String id ,ListActivities listActivities,String mode){
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
