package es.udc.psi.tt_ps.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Handler;

import es.udc.psi.tt_ps.R;
import es.udc.psi.tt_ps.databinding.ActivitySearchBinding;
import es.udc.psi.tt_ps.ui.adapter.ListUsersAdapter;
import es.udc.psi.tt_ps.ui.adapter.ListUsersAdapter;
import es.udc.psi.tt_ps.ui.view.DetailsActivity;
import es.udc.psi.tt_ps.ui.viewmodel.ActivityListsPres;
import es.udc.psi.tt_ps.ui.viewmodel.ActivityViewModel;
import es.udc.psi.tt_ps.ui.viewmodel.ListUsers;
import es.udc.psi.tt_ps.ui.viewmodel.ListUsers;
import es.udc.psi.tt_ps.ui.viewmodel.UserListPres;
import es.udc.psi.tt_ps.ui.viewmodel.UserViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    String TAG = "_TAG";
    String ACTIVITY = "MainActivity2";
    List<ListUsers> usersList;
    UserListPres presenter = new UserListPres();
    RecyclerView recyclerView;

    ListUsersAdapter listUsersAdapter;
    List<String> tags = new ArrayList<>();
    List<Float> range = new ArrayList<>();
    FusedLocationProviderClient mFusedLocationClient;
    LocationManager mLocationManager;
    GeoLocation mLocation;
    private ActivityListFragment.FragmentListener listener;
    ActivitySearchBinding binding;
    UserViewModel userViewModel;


    public SearchFragment() {
        // Required empty public constructor
    }


    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();

        return fragment;
    }



    public interface FragmentListener {
        void onFragmentInteraction(List<String> tags, List<Float> range, GeoLocation location,
                                   ListUsersAdapter listUsersAdapter, ActivityListsPres presenter,
                                   RecyclerView recyclerView);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = ActivitySearchBinding.inflate(inflater,container,false);
        tags = Arrays.asList(getResources().getStringArray(R.array.interests_array));
        range = Arrays.asList(0f, 60f);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity().getApplicationContext());


        binding.searchButton.setOnClickListener(v -> {
                    try {
                        initRecycledView(binding.searchUser.getText().toString());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });

        return binding.getRoot();
    }

    public void initRecycledView(String name) throws InterruptedException {
        usersList = new ArrayList<>();


        try {
            presenter.setRecycledDataFiltered(usersList,name);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        listUsersAdapter= new ListUsersAdapter(usersList,getActivity().getApplicationContext(), this::moreActivityInfo);
        userViewModel = new UserViewModel(listUsersAdapter);
        recyclerView = binding.usersRecycledView;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setAdapter(listUsersAdapter);

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
                       /* try {
                            presenter.updateRecycledDataFiltered(tags,recyclerView,range ,mLocation);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }*/

                        listUsersAdapter.notifyDataSetChanged();
                    }

                }
            }
        });


    }

    public void moreActivityInfo(ListUsers listUsers){
        //Metodo para ir a la vista detallada de actividades
        Toast.makeText(getContext(), "detallitos", Toast.LENGTH_SHORT).show();
    }




}