package es.udc.psi.tt_ps.ui.view;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import es.udc.psi.tt_ps.R;
import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.databinding.ActivityShowActivitiesBinding;
import es.udc.psi.tt_ps.ui.viewmodel.ActivityListsPres;
import es.udc.psi.tt_ps.ui.viewmodel.ListActivities;
import es.udc.psi.tt_ps.ui.viewmodel.ListActivitiesAdapter;

public class ActivityListActivities extends AppCompatActivity {
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
    boolean getMore = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        tags = Arrays.asList(getResources().getStringArray(R.array.interests_array));
        range = Arrays.asList(0f, 60f);
        super.onCreate(savedInstanceState);
        binding = ActivityShowActivitiesBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            initRecycledView();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        binding.floatingActionButtonAdd.setOnClickListener(v -> {
            Intent intentSend = new Intent(this, ActivityCreateActivity.class);
            startActivity(intentSend);
        });
        binding.filterButton.setOnClickListener(v -> {
            showFilterDialog();

        });

    }

    private void getLocation() throws InterruptedException {
        mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);


        Result<GeoLocation, Exception> res = new Result<>();

            Thread thread = new Thread(() -> {
                Looper.prepare();
                try {
                    Location bestLocation = null;
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                        return;
                    }
                    for (String provider : providers) {
                        Location l = mLocationManager.getLastKnownLocation(provider);
                        if (l == null) {
                            continue;
                        }
                        if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                            // Found best last known location: %s", l);
                            bestLocation = l;
                        }
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

    private void applySavedFilters(BottomSheetDialog bottomSheetDialog){
        bottomSheetDialog.setContentView(R.layout.filter_dialog);
        ChipGroup cg = bottomSheetDialog.findViewById(R.id.chip_group);
        for (int i = 0; i < cg.getChildCount(); i++) {
            Chip chip = (Chip) cg.getChildAt(i);
            if (tags.contains(chip.getText().toString().toLowerCase(Locale.ROOT))) {
                chip.setChecked(true);
            }
        }

        RangeSlider slider = bottomSheetDialog.findViewById(R.id.range_slider);
        slider.setValues(range);
    }

    private void showFilterDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);

        applySavedFilters(bottomSheetDialog);

        bottomSheetDialog.show();
        Button button = bottomSheetDialog.findViewById(R.id.button_save);


        assert button != null;
        button.setOnClickListener(v -> {

            ChipGroup chipGroup = bottomSheetDialog.findViewById(R.id.chip_group);
            List<String> filter_tags = new ArrayList<>();
            for (int i = 0; i < chipGroup.getChildCount(); i++) {
                Chip chip = (Chip) chipGroup.getChildAt(i);
                if (chip.isChecked()) {
                    filter_tags.add(chip.getText().toString().toLowerCase(Locale.ROOT));
                }
            }

            if (filter_tags.size() != 0) {
                tags = filter_tags;
                RangeSlider slider = bottomSheetDialog.findViewById(R.id.range_slider);
                range = slider.getValues();
                //get last item from recycler view
                int lastItem = listActivitiesAdapter.getItemCount();
                try {
                    presenter.setRecycledDataFiltered(tags, range, mLocation, recyclerView);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bottomSheetDialog.dismiss();

                listActivitiesAdapter.notifyDataSetChanged();

            } else {
                Snackbar.make(v, "No tags selected", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

        });


    }

    public void initRecycledView() throws InterruptedException {
        Log.d(TAG, ACTIVITY + " start init");
        activitiesList = new ArrayList<>();
        getLocation();
        try {
            presenter.setRecycledDataFiltered(tags, activitiesList,range ,mLocation);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        listActivitiesAdapter= new ListActivitiesAdapter(activitiesList,this, this::moreActivityInfo);
        recyclerView = binding.listRecycledView;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listActivitiesAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && presenter.getMore) {
                    int totalItems = Objects.requireNonNull(recyclerView.getAdapter()).getItemCount();
                    int lastVisibleItem = ((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager())).findLastVisibleItemPosition();
                    Log.d(TAG,ACTIVITY+" totalItems: "+totalItems+" lastVisibleItem: "+lastVisibleItem);
                    Log.d(TAG,ACTIVITY+" Comprobacion: " + (lastVisibleItem == totalItems - 1));
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
        Intent intent = new Intent(this,DetailsActivity.class);
        intent.putExtra("events", listActivities);
        intent.putExtra("latitud",listActivities.getLocation().getLatitude());
        intent.putExtra("longitud",listActivities.getLocation().getLongitude());
        startActivity(intent);
    }
}
