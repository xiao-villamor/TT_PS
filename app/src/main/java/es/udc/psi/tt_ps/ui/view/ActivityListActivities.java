package es.udc.psi.tt_ps.ui.view;



import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import es.udc.psi.tt_ps.R;
import es.udc.psi.tt_ps.data.network.user.userService;
import es.udc.psi.tt_ps.ui.viewmodel.ActivityListsPres;
import es.udc.psi.tt_ps.ui.viewmodel.ListActivities;
import es.udc.psi.tt_ps.ui.viewmodel.ListActivitiesAdapter;

public class ActivityListActivities extends AppCompatActivity {
    String TAG = "_TAG";
    String ACTIVITY = "MainActivity2";
    List<ListActivities> activitiesList;
    ActivityListsPres presenter = new ActivityListsPres();
    RecyclerView recyclerView ;
    //userService user = new userService();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_activities);
        Log.d(TAG,ACTIVITY+" onCreate");
        //user.loginUser("dev@mail.com","123456");
        initRecycledView();

    }

    public void initRecycledView(){
        Log.d(TAG,ACTIVITY+" start init");
        activitiesList = new ArrayList<>();
        presenter.setRecycledData(activitiesList);

        ListActivitiesAdapter listActivitiesAdapter= new ListActivitiesAdapter(activitiesList,this, ActivityListsPres::moreActivityInfo);
        recyclerView = findViewById(R.id.listRecycledView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listActivitiesAdapter);
        Log.d(TAG,ACTIVITY+" end init");

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
