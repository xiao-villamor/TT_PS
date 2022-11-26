package es.udc.psi.tt_ps.ui.view;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.google.firebase.firestore.auth.User;


import java.util.ArrayList;
import java.util.List;

import es.udc.psi.tt_ps.R;
import es.udc.psi.tt_ps.core.firebaseConnection;
import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.data.model.UserModel;
import es.udc.psi.tt_ps.databinding.ActivityUserInfoBinding;
import es.udc.psi.tt_ps.domain.user.getUserInfoUseCase;
import es.udc.psi.tt_ps.ui.adapter.tagAdapter;
import es.udc.psi.tt_ps.ui.viewmodel.ActivityListsPres;
import es.udc.psi.tt_ps.ui.viewmodel.ListActivities;
import es.udc.psi.tt_ps.ui.viewmodel.ListActivitiesAdapter;
import es.udc.psi.tt_ps.ui.viewmodel.UserActivityListPres;


public class UserInfoActivity extends AppCompatActivity {

    private ActivityUserInfoBinding binding;
    String TAG = "_TAG";
    String ACTIVITY = "MainActivity2";
    List<ListActivities> activitiesList;
    UserActivityListPres presenter = new UserActivityListPres();
    RecyclerView recyclerView ;

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
        binding.desc.setText(res.getDescription());

        initRecycledView();


    }



    public void initRecycledView(){
        activitiesList = new ArrayList<>();
        try {
            presenter.setRecycledData(activitiesList);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ListActivitiesAdapter listActivitiesAdapter= new ListActivitiesAdapter(activitiesList,this, UserActivityListPres::moreActivityInfo);
        recyclerView = binding.userAct;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listActivitiesAdapter);

        Log.d(TAG,ACTIVITY+" end init");



    }
}
