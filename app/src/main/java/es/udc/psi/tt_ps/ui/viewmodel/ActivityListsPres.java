package es.udc.psi.tt_ps.ui.viewmodel;

import static es.udc.psi.tt_ps.domain.activity.getFirstActivitiesUseCase.getActivities;
import static es.udc.psi.tt_ps.domain.activity.getNextActivitiesUseCase.getNextActivities;

import android.graphics.PointF;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import es.udc.psi.tt_ps.R;
import es.udc.psi.tt_ps.data.model.ActivityModel;
import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.data.repository.activityRepository;
import es.udc.psi.tt_ps.domain.activity.getFirstActivitiesUseCase;
import es.udc.psi.tt_ps.domain.activity.getNextActivitiesUseCase;

public class ActivityListsPres extends RecyclerView.OnScrollListener {
    activityRepository ar = new activityRepository();

    public static void moreActivityInfo(ListActivities ListActivities){
        //Metodo para ir a la vista detallada de actividades
        Log.d("TAG", "Mostrar en detalle" );
        //Intent intent = new Intent(this,ActivityListActivities.class);
        //intent.putExtra("events", ListActivities);
        //startActivity(intent);
    }

    public void setRecycledData(List<ListActivities> listActivities) throws InterruptedException {
        Log.d("_TAG","Presenter "+" start init");

        Result<List<ActivityModel>, Exception> data ;

        data = getActivities();

        List<ActivityModel> res = new ArrayList<>(data.data);

        for (int i=0; i<res.size();i++){
            listActivities.add(new ListActivities(R.drawable.bike_kick_push_scooter_svgrepo_com,res.get(i).getTitle(),
                    new PointF((float) 43.36854217446916, (float) -8.415802771112226), res.get(i).getStart_date(),
                    res.get(i).getDescription()));
        }
    }

    //función para it2, se usará para actualizar los datos del recycled view
    public void updateRecycledData(RecyclerView recyclerView) throws InterruptedException {
        Log.d("_TAG","Presenter "+" start init");

        Result<List<ActivityModel>, Exception> data ;

        data = getNextActivities();

        List<ActivityModel> res = new ArrayList<>(data.data);
        ListActivitiesAdapter adapter = (ListActivitiesAdapter) recyclerView.getAdapter();
        assert adapter != null;
        List<ListActivities> listActivities = adapter.getmData();

        for (int i=0; i<res.size();i++){
            listActivities.add(new ListActivities(R.drawable.bike_kick_push_scooter_svgrepo_com,res.get(i).getTitle(),
                    new PointF((float) 43.36854217446916, (float) -8.415802771112226), res.get(i).getStart_date(),
                    res.get(i).getDescription()));
        }
        adapter.setItems(listActivities);
        recyclerView.setAdapter(adapter);
    }

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
}
