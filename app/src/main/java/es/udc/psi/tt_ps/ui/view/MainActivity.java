package es.udc.psi.tt_ps.ui.view;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.firebase.geofire.GeoLocation;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import es.udc.psi.tt_ps.R;
import es.udc.psi.tt_ps.core.firebaseConnection;
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
    private static MainActivity mInstance;

    public static MainActivity getInstance(){
        return mInstance;
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {

            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    1);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
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

        requestPermissionsIfNecessary(new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE,
        });

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


    }

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
                Snackbar.make(v, "No tags selected", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

        });
    }

    public void dataChanged(String id, ListActivities listActivities,String mode){
        Log.d("dataChanged", "dataChanged");
        fragment.dataChanged(id,listActivities,mode);

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
                    return new SavedActivitiesFragment();

                case 3:
                    return new UserInfoFragment();

                default:
                    return null;
            }
        }
    }


}