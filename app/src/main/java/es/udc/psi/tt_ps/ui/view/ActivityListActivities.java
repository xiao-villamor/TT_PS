package es.udc.psi.tt_ps.ui.view;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import org.checkerframework.checker.units.qual.A;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import es.udc.psi.tt_ps.R;
import es.udc.psi.tt_ps.data.model.ActivityModel;
import es.udc.psi.tt_ps.data.repository.activityRepository;
import es.udc.psi.tt_ps.ui.viewmodel.ListActivities;
import es.udc.psi.tt_ps.ui.viewmodel.ListActivitiesAdapter;

public class ActivityListActivities extends AppCompatActivity {
    String TAG = "_TAG";
    String ACTIVITY = "MainActivity2";
    List<ListActivities> activitiesList;
    String text;
    ListActivitiesAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_activities);
        Log.d(TAG,ACTIVITY+" onCreate");
        Intent receiveIntent = getIntent();
        text = receiveIntent.getStringExtra("text");
        Log.d(TAG,ACTIVITY+" "+text);

        init();

    }

    public void init(){
        Log.d(TAG,ACTIVITY+" start init");
        activitiesList = new ArrayList<>();
        setData(activitiesList);

        ListActivitiesAdapter listActivitiesAdapter= new ListActivitiesAdapter(activitiesList,this,this::moveToDescription);
        RecyclerView recyclerView = findViewById(R.id.listRecycledView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listActivitiesAdapter);
        Log.d(TAG,ACTIVITY+" end init");
    }

    public void moveToDescription(ListActivities ListActivities){
        Log.d("TAG", "Mostrar en detalle" );
        Intent intent = new Intent(this,ActivityListActivities.class);
        intent.putExtra("events", ListActivities);
        startActivity(intent);
    }
    
    
    public void setData(List<ListActivities> listActivities){
        activityRepository ar = new activityRepository();
        AtomicReference<List<ActivityModel>> data = new AtomicReference<>();

        Thread thread = new Thread(){
            @Override
            public void run(){
                try {
                    data.set(ar.getActivities());
                } catch (TimeoutException | ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<ActivityModel> res = new ArrayList<>(data.get());

        for (int i=0; i<res.size();i++){
            listActivities.add(new ListActivities(R.drawable.bike_kick_push_scooter_svgrepo_com,res.get(i).getTitle(),
                    new PointF((float) 43.36854217446916, (float) -8.415802771112226), res.get(i).getStart_date(),
                    res.get(i).getDescription()));
        }
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
