package es.udc.psi.tt_ps.ui.view;


import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.app.ActionBar;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;


import es.udc.psi.tt_ps.R;
import es.udc.psi.tt_ps.core.firebaseConnection;
import es.udc.psi.tt_ps.data.model.ActivityModel;
import es.udc.psi.tt_ps.data.model.QueryResult;
import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.data.network.user.OnAuthStateChangeListener;
import es.udc.psi.tt_ps.data.repository.authRepository;
import es.udc.psi.tt_ps.databinding.ActivityMainBinding;
import es.udc.psi.tt_ps.ui.adapter.ListActivitiesAdapter;
import es.udc.psi.tt_ps.ui.fragments.ActivityListFragment;
import es.udc.psi.tt_ps.ui.fragments.NavigationBarFragment;
import es.udc.psi.tt_ps.ui.fragments.SavedActivitiesFragment;
import es.udc.psi.tt_ps.ui.fragments.SearchFragment;
import es.udc.psi.tt_ps.ui.fragments.UserInfoFragment;
import es.udc.psi.tt_ps.ui.viewmodel.ActivityListsPres;
import es.udc.psi.tt_ps.ui.viewmodel.ListActivities;
import es.udc.psi.tt_ps.ui.viewmodel.MainViewModel;
import es.udc.psi.tt_ps.domain.activity.getActivityUseCase;


public class MainActivity extends AppCompatActivity implements OnAuthStateChangeListener, ActivityListFragment.FragmentListener,NavigationBarFragment.FragmentListener {
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseStorage storage;
    private ActivityMainBinding binding;
    authRepository authRepository = new authRepository();
    MainViewModel mainViewModel = new MainViewModel(authRepository);
    private static final int NUM_PAGES = 4;
    private ViewPager2 viewPager2;
    private List<String> mTags;
    private List<Float> mRange;
    private GeoLocation mLocation;
    private ActivityListsPres mPresenter;
    private ListActivitiesAdapter mListActivitiesAdapter;
    private RecyclerView mRecycler;
    private  ScreenSlidePageAdapter adapter;
    private NavigationBarFragment navigationBarFragment;
    public ActivityListFragment fragment;
    public SavedActivitiesFragment fragmentS;
    private static MainActivity mInstance;
    String CHANNEL_ID ="1";
    int notificationid;
    Intent mServiceIntent;



    public static MainActivity getInstance(){
        return mInstance;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        String[] permissions = {Manifest.permission.INTERNET,Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE};

        permissionLauncherMultiple.launch(permissions);




        mInstance = this;
        mainViewModel.setAuthStateChangeListener(this);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        ActionBar actionBar = getActionBar();
        if(actionBar != null){
            actionBar.hide();
        }

        firebaseConnection connection = new firebaseConnection();
        connection.connect(this);

        adapter = new ScreenSlidePageAdapter(this);
        viewPager2 = binding.pager;
        viewPager2.setAdapter(adapter);
        navigationBarFragment = (NavigationBarFragment) getSupportFragmentManager().findFragmentByTag("navigation");
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                navigationBarFragment.onTabChanged(position);
            }
        });
        FCM();
        Bundle extras = getIntent().getExtras();
        if (extras != null){

            String update_id = extras.getString("update_id");
            String rating_id = extras.getString("rating_id");
            String count = extras.getString("count");
            String UUID = extras.getString("UUID");


            if(update_id != null){

                Result<ActivityModel,Exception> listActivities = null;
                try {
                    listActivities = getActivityUseCase.getActivityUseCase(update_id);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                ListActivities listActivities1 = new ListActivities(listActivities.data.getId(),listActivities.data.getImage(),listActivities.data.getTitle()
                        ,listActivities.data.getLocation(),listActivities.data.getEnd_date(),listActivities.data.getDescription()
                        ,listActivities.data.getStart_date(),listActivities.data.getCreation_date(),listActivities.data.getAdminId()
                        ,listActivities.data.getTags(),listActivities.data.getParticipants());

                Intent intent = new Intent(this, DetailsActivity.class);

                intent.putExtra("events", listActivities1);
                intent.putExtra("latitud", listActivities1.getLocation().getLatitude());
                intent.putExtra("longitud", listActivities1.getLocation().getLongitude());
                startActivity(intent);
            }else if (rating_id != null){


                Intent intent = new Intent(this, RatingActivity.class);

                intent.putExtra("id", rating_id);
                intent.putExtra("count", count);
                intent.putExtra("UUID", UUID);
                startActivity(intent);
            }


        }



    }

    private void FCM(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        Log.d("FCM", token);
                    }
                });
    }



    private final ActivityResultLauncher<String[]> permissionLauncherMultiple = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> result) {

                //Check if permissions are granted or not
                Log.d("TAG", "permissions requested");
                boolean allAreGranted = true;
                for(Boolean isGranted : result.values()){
                    Log.d("TAG","onActivityResult : isGranted " + isGranted);
                    allAreGranted = allAreGranted && isGranted;

                }

                if(allAreGranted){

                    //start app
                }else{
                    Toast.makeText(MainActivity.this, R.string.toast_permisionDenied,Toast.LENGTH_SHORT).show();
                    //close app
                    finish();

                }

            }
        });




    private void applySavedFilters(BottomSheetDialog bottomSheetDialog){
        bottomSheetDialog.setContentView(R.layout.filter_dialog);

        ChipGroup cg = bottomSheetDialog.findViewById(R.id.chip_group);

        for (int i = 0; i < cg.getChildCount(); i++) {
            Chip chip = (Chip) cg.getChildAt(i);
            if (mTags.contains(chip.getText().toString().toLowerCase(Locale.ROOT))) {
                chip.setChecked(true);
            }
        }

        RangeSlider slider = bottomSheetDialog.findViewById(R.id.range_slider);
        slider.setValues(mRange);
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
                mTags = filter_tags;
                RangeSlider slider = bottomSheetDialog.findViewById(R.id.range_slider);
                mRange = slider.getValues();
                //get last item from recycler view
                int lastItem = mListActivitiesAdapter.getItemCount();
                try {
                    mPresenter.setRecycledDataFiltered(mTags, mRange, mLocation, mRecycler);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bottomSheetDialog.dismiss();
                //get fragment using tag
                ActivityListFragment fragment = (ActivityListFragment) getSupportFragmentManager().findFragmentByTag("f" + viewPager2.getCurrentItem());
                fragment.updateList(mTags, mRange);
                mListActivitiesAdapter.notifyDataSetChanged();

            } else {
                Snackbar.make(v, R.string.snackBar_noTags, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

        });
    }

    public void dataChanged(String id, ListActivities listActivities,String mode){
        fragment.dataChanged(id,listActivities,mode);
    }

    public void dataChangedSaved(String id, ListActivities listActivities,String mode){
        fragmentS.dataChangedSaved(id,listActivities,mode);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mainViewModel.addAuthListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mainViewModel.removeAuthListener();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onAuthStateChanged(boolean isUserLoggedOut) {
        Intent userProfileIntent = new Intent(this, LogInActivity.class);
        startActivity(userProfileIntent);

    }

    @Override
    public void onFragmentInteraction(List<String> tags, List<Float> range, GeoLocation location, ListActivitiesAdapter listActivitiesAdapter, ActivityListsPres presenter, RecyclerView recyclerView) {
        mTags = tags;
        mRange = range;
        mLocation = location;
        mListActivitiesAdapter = listActivitiesAdapter;
        mPresenter = presenter;
        mRecycler = recyclerView;
        showFilterDialog();
    }

    @Override
    public void onFragmentInteraction(int number) {
        viewPager2.setCurrentItem(number);
    }


    private class ScreenSlidePageAdapter extends FragmentStateAdapter{
        public ScreenSlidePageAdapter(AppCompatActivity activity) {
            super(activity);
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }



        @Override
        public Fragment createFragment(int position) {

            switch (position) {
                case 0:
                    fragment = new ActivityListFragment();
                    return fragment;
                case 1:
                    return new SearchFragment();

                case 2:
                    fragmentS = new SavedActivitiesFragment();
                    return fragmentS;

                case 3:
                    return new UserInfoFragment();

                default:
                    return null;
            }
        }
    }

}