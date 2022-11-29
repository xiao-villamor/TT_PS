package es.udc.psi.tt_ps.ui.viewmodel;

import static es.udc.psi.tt_ps.domain.activity.getFirstActivitiesUseCase.getActivities;
import static es.udc.psi.tt_ps.domain.activity.getNextActivitiesUseCase.getNextActivities;

import android.graphics.PointF;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import es.udc.psi.tt_ps.R;
import es.udc.psi.tt_ps.data.model.ActivityModel;
import es.udc.psi.tt_ps.data.model.QueryResult;
import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.data.repository.activityRepository;
import es.udc.psi.tt_ps.domain.activity.getFirstActivitiesUseCase;
import es.udc.psi.tt_ps.domain.activity.getNextActivitiesUseCase;

public class ActivityListsPres extends RecyclerView.OnScrollListener {
    activityRepository ar = new activityRepository();
    private DocumentSnapshot prevDocSnap;



    public void setRecycledData(List<ListActivities> listActivities) throws InterruptedException {
        Log.d("_TAG", "Presenter " + " start init");

        Result<QueryResult<List<ActivityModel>, DocumentSnapshot>, Exception> data;

        data = getActivities();

        if (data.exception == null) {
            Log.d("_TAG", "Presenter " + " data not null");
            List<ActivityModel> res = new ArrayList<>(data.data.data);
            prevDocSnap = data.data.cursor;

            for (int i = 0; i < res.size(); i++) {
                /*listActivities.add(new ListActivities(res.get(i).getImage(), res.get(i).getTitle(),
                        new PointF((float) 43.36854217446916, (float) -8.415802771112226), res.get(i).getStart_date(),
                        res.get(i).getDescription()));*/
                listActivities.add(new ListActivities(res.get(i).getImage(),res.get(i).getTitle(),new PointF((float) 43.36854217446916, (float) -8.415802771112226),res.get(i).getEnd_date(),
                        res.get(i).getDescription(),res.get(i).getStart_date(),res.get(i).getCreation_date(),res.get(i).getAdminId(),0,
                        res.get(i).getTags()));
            }
        }
    }


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
                /*listActivities.add(new ListActivities(res.get(i).getImage(), res.get(i).getTitle(),
                        new PointF((float) 43.36854217446916, (float) -8.415802771112226), res.get(i).getStart_date(),
                        res.get(i).getDescription()));*/
                listActivities.add(new ListActivities(res.get(i).getImage(),res.get(i).getTitle(),new PointF((float) 43.36854217446916, (float) -8.415802771112226),res.get(i).getEnd_date(),
                        res.get(i).getDescription(),res.get(i).getStart_date(),res.get(i).getCreation_date(),res.get(i).getAdminId(),0,
                        res.get(i).getTags()));
            }
            adapter.setItems(listActivities);
        }else {
            Toast.makeText(recyclerView.getContext(), "No more Activities", Toast.LENGTH_SHORT).show();
        }
    }

    /*

    //listeners para poder mirar el estado del scroll
    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        Log.d("TAG","pasa por aqui joder");
        try {
            updateRecycledData(recyclerView);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
    }

     */
}
