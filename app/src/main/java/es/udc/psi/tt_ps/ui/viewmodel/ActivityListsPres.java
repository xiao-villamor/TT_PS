package es.udc.psi.tt_ps.ui.viewmodel;

import static es.udc.psi.tt_ps.domain.activity.getFirstActivitiesUseCase.getActivities;
import static es.udc.psi.tt_ps.domain.activity.getNextActivitiesUseCase.getNextActivities;
import static es.udc.psi.tt_ps.domain.activity.getFirstActivitiesFiltered.getActivitiesFiltered;
import static es.udc.psi.tt_ps.domain.activity.getNextActivitesFilteredUseCase.getActivitiesFilteredNext;



import android.graphics.PointF;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import es.udc.psi.tt_ps.data.model.ActivityModel;
import es.udc.psi.tt_ps.data.model.QueryResult;
import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.data.repository.activityRepository;


public class ActivityListsPres extends RecyclerView.OnScrollListener {
    activityRepository ar = new activityRepository();
    private DocumentSnapshot prevDocSnap;

    public static void moreActivityInfo(ListActivities ListActivities){
        //Metodo para ir a la vista detallada de actividades
        Log.d("TAG", "Mostrar en detalle" );
        //Intent intent = new Intent(this,ActivityListActivities.class);
        //intent.putExtra("events", ListActivities);
        //startActivity(intent);
    }

    //DEPRECATED
    public void setRecycledData(List<ListActivities> listActivities) throws InterruptedException {
        Log.d("_TAG", "Presenter " + " start init");

        Result<QueryResult<List<ActivityModel>, DocumentSnapshot>, Exception> data;

        data = getActivities();

        if (data.exception == null) {
            Log.d("_TAG", "Presenter " + " data not null");
            List<ActivityModel> res = new ArrayList<>(data.data.data);
            prevDocSnap = data.data.cursor;

            for (int i = 0; i < res.size(); i++) {
                listActivities.add(new ListActivities(res.get(i).getImage(), res.get(i).getTitle(),
                        new PointF((float) 43.36854217446916, (float) -8.415802771112226), res.get(i).getStart_date(),
                        res.get(i).getDescription()));
            }
        }
    }

    public void setRecycledDataFiltered(List<String> tags,List<Float> range,RecyclerView recyclerView) throws InterruptedException {
        Result<QueryResult<List<ActivityModel>, DocumentSnapshot>, Exception> data;
        data = getActivitiesFiltered(tags,range);
        if (data.exception == null) {
            Log.d("_TAG", "Presenter " + " data not null");
            List<ActivityModel> res = new ArrayList<>(data.data.data);
            prevDocSnap = data.data.cursor;


            ListActivitiesAdapter adapter = (ListActivitiesAdapter) recyclerView.getAdapter();
            assert adapter != null;
            List<ListActivities> listActivities = new ArrayList<>();

            for (int i = 0; i < res.size(); i++) {
                listActivities.add(new ListActivities(res.get(0).getImage(), res.get(i).getTitle(),
                        new PointF((float) 43.36854217446916, (float) -8.415802771112226), res.get(i).getStart_date(),
                        res.get(i).getDescription()));
            }
            adapter.setItems(listActivities);
        }
    }

    public void setRecycledDataFiltered(List<String> tags, List<ListActivities> listActivities) throws InterruptedException {
        Result<QueryResult<List<ActivityModel>, DocumentSnapshot>, Exception> data;

        List<Float> range = new ArrayList<>();

        range.add((float) 0);
        range.add((float) 20);

        data = getActivitiesFiltered(tags,range);
        if (data.exception == null) {
            Log.d("_TAG", "Presenter " + " data not null");
            List<ActivityModel> res = new ArrayList<>(data.data.data);
            prevDocSnap = data.data.cursor;

            for (int i = 0; i < res.size(); i++) {
                listActivities.add(new ListActivities(res.get(i).getImage(), res.get(i).getTitle(),
                        new PointF((float) 43.36854217446916, (float) -8.415802771112226), res.get(i).getStart_date(),
                        res.get(i).getDescription()));
            }
        }
    }

    public void updateRecycledDataFiltered(List<String> tags,RecyclerView recyclerView) throws InterruptedException {
        Result<QueryResult<List<ActivityModel>, DocumentSnapshot>, Exception> data;
        List<Float> range = new ArrayList<>();
        range.add((float) 0);
        range.add((float) 20);
        data = getActivitiesFilteredNext(tags,range,prevDocSnap);
        if (data.exception == null && data.data.cursor != null) {
            Log.d("_TAG", "Presenter " + " data not null");
            List<ActivityModel> res = new ArrayList<>(data.data.data);
            prevDocSnap = data.data.cursor;
            ListActivitiesAdapter adapter = (ListActivitiesAdapter) recyclerView.getAdapter();
            assert adapter != null;
            List<ListActivities> listActivities = adapter.getmData();


            for (int i = 0; i < res.size(); i++) {
                listActivities.add(new ListActivities(res.get(0).getImage(), res.get(i).getTitle(),
                        new PointF((float) 43.36854217446916, (float) -8.415802771112226), res.get(i).getStart_date(),
                        res.get(i).getDescription()));
            }
            adapter.setItems(listActivities);
        }

    }

    //función para it2, se usará para actualizar los datos del recycled view
    //DEPRECATED
    public void updateRecycledData(RecyclerView recyclerView) throws InterruptedException {
        Log.d("_TAG","Presenter "+" start init");

        Result< QueryResult<List<ActivityModel>,DocumentSnapshot>, Exception> data ;

        data = getNextActivities(prevDocSnap);

        if(data.exception == null && data.data.cursor != null){

            List<ActivityModel> res = new ArrayList<>(data.data.data);
            prevDocSnap = data.data.cursor;
            ListActivitiesAdapter adapter = (ListActivitiesAdapter) recyclerView.getAdapter();
            assert adapter != null;
            List<ListActivities> listActivities = adapter.getmData();

            for (int i = 0; i < res.size(); i++) {
                listActivities.add(new ListActivities(res.get(0).getImage(), res.get(i).getTitle(),
                        new PointF((float) 43.36854217446916, (float) -8.415802771112226), res.get(i).getStart_date(),
                        res.get(i).getDescription()));
            }
            adapter.setItems(listActivities);
        }
    }

}
