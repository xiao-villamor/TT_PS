package es.udc.psi.tt_ps.ui.view;



import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;
import es.udc.psi.tt_ps.R;
import es.udc.psi.tt_ps.data.network.user.userService;
import es.udc.psi.tt_ps.databinding.ActivityShowActivitiesBinding;
import es.udc.psi.tt_ps.databinding.ActivityUserInfoBinding;
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
    //userService user = new userService();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowActivitiesBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        Log.d(TAG,ACTIVITY+" onCreate");
        initRecycledView();
        binding.filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,ACTIVITY+" onClick");
                showFilterDialog();
            }
        });

    }

    private void showFilterDialog(){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.filter_dialog);
        bottomSheetDialog.show();


    }

    public void initRecycledView(){
        Log.d(TAG,ACTIVITY+" start init");
        activitiesList = new ArrayList<>();
        try {
            presenter.setRecycledData(activitiesList);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ListActivitiesAdapter listActivitiesAdapter= new ListActivitiesAdapter(activitiesList,this, ActivityListsPres::moreActivityInfo);
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
                    if (lastVisibleItem == totalItems - 1) {
                        Log.d(TAG,ACTIVITY+" onScrollStateChanged");
                        try {
                            presenter.updateRecycledData(recyclerView);
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
