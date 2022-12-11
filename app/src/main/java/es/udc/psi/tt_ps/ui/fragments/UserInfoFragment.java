package es.udc.psi.tt_ps.ui.fragments;

import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.udc.psi.tt_ps.core.firebaseConnection;
import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.data.model.UserModel;
import es.udc.psi.tt_ps.data.repository.authRepository;
import es.udc.psi.tt_ps.databinding.ActivityUserInfoBinding;
import es.udc.psi.tt_ps.domain.user.getUserInfoUseCase;
import es.udc.psi.tt_ps.ui.adapter.tagAdapter;
import es.udc.psi.tt_ps.ui.view.DetailsActivity;
import es.udc.psi.tt_ps.ui.viewmodel.ListActivities;
import es.udc.psi.tt_ps.ui.adapter.ListActivitiesAdapter;
import es.udc.psi.tt_ps.ui.viewmodel.MainViewModel;
import es.udc.psi.tt_ps.ui.viewmodel.UserActivityListPres;


public class UserInfoFragment extends Fragment {

    private ActivityUserInfoBinding binding;
    String TAG = "_TAG";
    String ACTIVITY = "MainActivity2";
    List<ListActivities> activitiesList;
    UserActivityListPres presenter = new UserActivityListPres();
    RecyclerView recyclerView ;
    authRepository authRepository = new authRepository();
    MainViewModel mainViewModel = new MainViewModel(authRepository);

    public static UserInfoFragment newInstance() {
        return new UserInfoFragment();
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = ActivityUserInfoBinding.inflate(inflater, container, false);
        Result<UserModel, Exception> u = new Result<>();
        UserModel res = null;
        Log.d("_TAG",firebaseConnection.getUser().toString());
        try {
            u = getUserInfoUseCase.getInfo(firebaseConnection.getUser());

            if(u.exception == null & u.data != null){

                res = u.data;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        try {
            Glide.with(this).load(res.getProfilePic()).into(binding.profilePic);


        }catch (Exception e){
            Log.d("_TAG","no profile pic");
        }

        binding.Name.setText(res.getName());
        binding.rating.setRating(res.getRating().get(0));
        tagAdapter tagAdapter = new tagAdapter(res.getInterests().toArray(new String[0]));
        binding.simpleGridView.setAdapter(tagAdapter);
        if(res.getDescription() != null){
            binding.desc.setText(res.getDescription());
        }
        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        binding.logout.setOnClickListener(view1 -> {
            mainViewModel.signOut();

        });

        initRecycledView();

        return binding.getRoot();

    }

    /*

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserInfoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        Result<UserModel, Exception> u = new Result<>();
        UserModel res = null;
        Log.d("_TAG",firebaseConnection.getUser().toString());
        try {
         u = getUserInfoUseCase.getInfo(firebaseConnection.getUser());

         if(u.exception == null & u.data != null){

             res = u.data;
         }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        try {
           Glide.with(this).load(res.getProfilePic()).into(binding.profilePic);


        }catch (Exception e){
            Log.d("_TAG","no profile pic");
        }

        binding.Name.setText(res.getName());
        binding.rating.setRating(res.getRating().get(0));
        tagAdapter tagAdapter = new tagAdapter(res.getInterests().toArray(new String[0]));
        binding.simpleGridView.setAdapter(tagAdapter);
        if(res.getDescription() != null){
            binding.desc.setText(res.getDescription());
        }

        initRecycledView();

    }


     */



    public void initRecycledView(){
        activitiesList = new ArrayList<>();
        try {
            presenter.setRecycledData(activitiesList,getActivity().getApplicationContext());
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }

        ListActivitiesAdapter listActivitiesAdapter= new ListActivitiesAdapter(activitiesList,getActivity().getApplicationContext(), this::moreActivityInfo);

        recyclerView = binding.userAct;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setAdapter(listActivitiesAdapter);
    }

    public void moreActivityInfo(ListActivities listActivities){
        Intent intent = new Intent(getActivity().getApplicationContext(), DetailsActivity.class);
        intent.putExtra("events", listActivities);
        intent.putExtra("latitud",listActivities.getLocation().getLatitude());
        intent.putExtra("longitud",listActivities.getLocation().getLongitude());
        startActivity(intent);

    }
}
