package es.udc.psi.tt_ps.ui.fragments;

import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.GeoPoint;


import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import es.udc.psi.tt_ps.R;
import es.udc.psi.tt_ps.core.firebaseConnection;
import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.data.model.UserModel;
import es.udc.psi.tt_ps.data.repository.authRepository;
import es.udc.psi.tt_ps.databinding.ActivityUserInfoBinding;
import es.udc.psi.tt_ps.domain.user.getUserInfoUseCase;
import es.udc.psi.tt_ps.ui.adapter.tagAdapter;
import es.udc.psi.tt_ps.ui.view.DetailsActivity;
import es.udc.psi.tt_ps.ui.view.EditActivity;
import es.udc.psi.tt_ps.ui.view.EditUser;
import es.udc.psi.tt_ps.ui.view.RegisterUserActivity;
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


        init();
        /*el codigo que estaba aqui y que inicializaba la actividad lo pase tal cual (no modifique nada) al
        * metodo init() init() para que asi tambien se pueda ejecutar este codigo al terminar de editar la
        * informacion y se muestre la info del usuario actualizada acorde a lo modificado*/

        return binding.getRoot();

    }

    void init(){
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
        Log.d("_TAG",res.getRating().toString());
        binding.rating.setRating(res.getRating());
        binding.ratingCnt.setText("("+res.getRatingCount()+")");
        tagAdapter tagAdapter = new tagAdapter(res.getInterests().toArray(new String[0]));
        binding.simpleGridView.setAdapter(tagAdapter);
        if(res.getDescription() != null){
            binding.desc.setText(res.getDescription());
        }
        /*
        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        binding.logout.setOnClickListener(view1 -> {
            mainViewModel.signOut();

        });

         */

        binding.editUserButton.setOnClickListener(this::showMenu);

        initRecycledView();
    }


    ActivityResultLauncher<Intent> startEditUser = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                    Intent data = result.getData();
                    Bundle bundle = data.getExtras();
                    if (bundle!= null) {
                        Boolean b = (Boolean) bundle.get("edited");
                        if(b){
                            init();
                        }

                    }
                }
            }
    );

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

    public void showMenu(View v){
        PopupMenu popupMenu = new PopupMenu(getActivity().getApplicationContext(),v);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.d(TAG, "onMenuItemClick: " + item.getItemId());
                switch (item.getItemId()){
                    case R.id.option_1:
                        Intent intent = new Intent(getContext(), EditUser.class);
                        intent.putExtra("uuid", firebaseConnection.getUser());
                        startEditUser.launch(intent);
                        return true;
                    case R.id.option_2:
                        mainViewModel.signOut();
                        return true;
                    default:
                        return false;
                }
            }


        });
        popupMenu.inflate(R.menu.pop_up_menu);
        popupMenu.show();
    }

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
