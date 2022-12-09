package es.udc.psi.tt_ps.ui.viewmodel;


import static es.udc.psi.tt_ps.domain.activity.getFirstActivitiesFiltered.getActivitiesFiltered;
import static es.udc.psi.tt_ps.domain.activity.getNextActivitesFilteredUseCase.getActivitiesFilteredNext;




import android.util.Log;


import androidx.recyclerview.widget.RecyclerView;

import com.firebase.geofire.GeoLocation;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import es.udc.psi.tt_ps.data.model.ActivityModel;
import es.udc.psi.tt_ps.data.model.QueryResult;
import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.data.repository.activityRepository;
import es.udc.psi.tt_ps.ui.adapter.ListActivitiesAdapter;


public class ActivityListsPres extends RecyclerView.OnScrollListener {
    activityRepository ar = new activityRepository();
    private DocumentSnapshot prevDocSnap;
    public boolean getMore = true;
    public List<String> participants = new ArrayList<>();



    public void setRecycledDataFiltered(List<String> tags, List<Float> range, GeoLocation location, RecyclerView recyclerView) throws InterruptedException {

        Result<QueryResult<List<ActivityModel>,List<DocumentSnapshot>>, Exception> data;


        ListActivitiesAdapter adapter = (ListActivitiesAdapter) recyclerView.getAdapter();

        data = getActivitiesFiltered(tags,range,location);
        if(data.data.data.size() == 0){
            Snackbar.make(recyclerView.getRootView(), "No hay actividades que coincidan con los filtros", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
                adapter.setItems(new ArrayList<>());
        }else {

            if (data.exception == null && data.data.cursor != null) {
                List<ActivityModel> res = new ArrayList<>(data.data.data);
                List<DocumentSnapshot> doc = new ArrayList<>(data.data.cursor);
                if(doc.size()>0){
                    prevDocSnap=doc.get(doc.size()-1);
                }


                assert adapter != null;
                List<ListActivities> listActivities = new ArrayList<>();

                for (int i = 0; i < res.size(); i++) {
                    if (res.get(i).getParticipants()!=null){
                        participants=res.get(i).getParticipants();
                    }
                    listActivities.add(new ListActivities(doc.get(i).getId(),res.get(i).getImage(),res.get(i).getTitle(),res.get(i).getLocation(),res.get(i).getEnd_date(),
                            res.get(i).getDescription(),res.get(i).getStart_date(),res.get(i).getCreation_date(),res.get(i).getAdminId(),participants,
                            res.get(i).getTags()));

                }
                adapter.setItems(listActivities);
            }else{
                getMore = false;
            }
        }
    }

    public void setRecycledDataFiltered(List<String> tags, List<ListActivities> listActivities,List<Float> range,GeoLocation location) throws InterruptedException {
        Result<QueryResult<List<ActivityModel>,List<DocumentSnapshot>>, Exception> data;


        data = getActivitiesFiltered(tags,range,location);
        if (data.exception == null && data.data.data.size() > 0) {
            Log.d("_TAG", "Presenter " + " data not null");
            List<ActivityModel> res = new ArrayList<>(data.data.data);
            List<DocumentSnapshot> doc = new ArrayList<>(data.data.cursor);
            if(doc.size()>0){
                prevDocSnap=doc.get(doc.size()-1);
            }

            for (int i = 0; i < res.size(); i++) {
                if (res.get(i).getParticipants()!=null){
                    participants=res.get(i).getParticipants();
                }
                listActivities.add(new ListActivities(doc.get(i).getId(),res.get(i).getImage(),res.get(i).getTitle(),res.get(i).getLocation(),res.get(i).getEnd_date(),
                        res.get(i).getDescription(),res.get(i).getStart_date(),res.get(i).getCreation_date(),res.get(i).getAdminId(),participants,
                        res.get(i).getTags()));

            }

        }
    }

    public void updateRecycledDataFiltered(List<String> tags,RecyclerView recyclerView,List<Float> range,GeoLocation location) throws InterruptedException {
        Result<QueryResult<List<ActivityModel>,List<DocumentSnapshot>>, Exception> data;

        data = getActivitiesFilteredNext(tags,range,prevDocSnap,location);
        if (data.exception == null && data.data.cursor != null) {
            Log.d("_TAG", "Presenter " + " data not null");
            List<ActivityModel> res = new ArrayList<>(data.data.data);
            List<DocumentSnapshot> doc = new ArrayList<>(data.data.cursor);
            if(doc.size()>0){
                prevDocSnap=doc.get(doc.size()-1);
            }
            ListActivitiesAdapter adapter = (ListActivitiesAdapter) recyclerView.getAdapter();

            assert adapter != null;
            List<ListActivities> listActivities = adapter.getmData();

            for (int i = 0; i < res.size(); i++) {
                if (res.get(i).getParticipants()!=null){
                    participants=res.get(i).getParticipants();
                }
                listActivities.add(new ListActivities(doc.get(i).getId(),res.get(i).getImage(),res.get(i).getTitle(),res.get(i).getLocation(),res.get(i).getEnd_date(),
                        res.get(i).getDescription(),res.get(i).getStart_date(),res.get(i).getCreation_date(),res.get(i).getAdminId(),participants,
                        res.get(i).getTags()));

            }
            adapter.setItems(listActivities);
        }else{
            getMore = false;

        }

    }

}
