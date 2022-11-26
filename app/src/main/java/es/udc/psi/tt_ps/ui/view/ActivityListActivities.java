package es.udc.psi.tt_ps.ui.view;



import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.RangeSlider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import es.udc.psi.tt_ps.R;
import es.udc.psi.tt_ps.databinding.ActivityShowActivitiesBinding;
import es.udc.psi.tt_ps.ui.viewmodel.ActivityListsPres;
import es.udc.psi.tt_ps.ui.viewmodel.ListActivities;
import es.udc.psi.tt_ps.ui.viewmodel.ListActivitiesAdapter;

public class ActivityListActivities extends AppCompatActivity {
    String TAG = "_TAG";
    String ACTIVITY = "MainActivity2";
    List<ListActivities> activitiesList;
    ActivityListsPres presenter = new ActivityListsPres();
    RecyclerView recyclerView ;
    ActivityShowActivitiesBinding binding;
    ListActivitiesAdapter listActivitiesAdapter;
    List<String> tags = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        tags = Arrays.asList(getResources().getStringArray(R.array.interests_array));

        Log.d(TAG, "_TAG: " + tags);

        super.onCreate(savedInstanceState);
        binding = ActivityShowActivitiesBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        Log.d(TAG,ACTIVITY+" onCreate");
        initRecycledView();
        binding.filterButton.setOnClickListener(v -> {
            Log.d(TAG,ACTIVITY+" onClick");
            showFilterDialog();

        });

    }

    private void showFilterDialog(){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.filter_dialog);
        ChipGroup cg = bottomSheetDialog.findViewById(R.id.chip_group);
        for (int i = 0; i < cg.getChildCount(); i++) {
            Chip chip = (Chip)cg.getChildAt(i);
            if (tags.contains(chip.getText().toString().toLowerCase(Locale.ROOT))){
                chip.setChecked(true);
            }
        }

        bottomSheetDialog.show();
        Button button = bottomSheetDialog.findViewById(R.id.button_save);
        button.setOnClickListener(v -> {

            ChipGroup chipGroup = bottomSheetDialog.findViewById(R.id.chip_group);
            List<String> filter_tags = new ArrayList<>();
            for (int i = 0; i < chipGroup.getChildCount(); i++) {
                Chip chip = (Chip)chipGroup.getChildAt(i);
                if (chip.isChecked()) {
                    filter_tags.add(chip.getText().toString().toLowerCase(Locale.ROOT));
                }
            }
            RangeSlider slider = bottomSheetDialog.findViewById(R.id.range_slider);
            List<Float> values = slider.getValues();
            tags = filter_tags;

            Log.d(TAG,ACTIVITY+" tags: "+tags);
            try {
                presenter.setRecycledDataFiltered(tags,values,recyclerView);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            listActivitiesAdapter.notifyDataSetChanged();

            bottomSheetDialog.dismiss();


        });



    }

    public void initRecycledView(){
        Log.d(TAG,ACTIVITY+" start init");
        activitiesList = new ArrayList<>();
        try {
            presenter.setRecycledDataFiltered(tags, activitiesList);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        listActivitiesAdapter= new ListActivitiesAdapter(activitiesList,this, ActivityListsPres::moreActivityInfo);
        recyclerView = binding.listRecycledView;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listActivitiesAdapter);

        Log.d(TAG,ACTIVITY+" end init");

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    int totalItems = recyclerView.getAdapter().getItemCount();
                    int lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                    Log.d(TAG,ACTIVITY+" totalItems: "+totalItems+" lastVisibleItem: "+lastVisibleItem);
                    if (lastVisibleItem == totalItems - 1) {
                        try {
                            presenter.updateRecycledDataFiltered(tags,recyclerView);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        listActivitiesAdapter.notifyItemInserted(activitiesList.size());
                    }

                }
            }
        });

    }



    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,ACTIVITY+" onStart");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,ACTIVITY+" onDestroy");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,ACTIVITY+" onStop");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,ACTIVITY+" onPause");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,ACTIVITY+" onResume");
    }
}
