package es.udc.psi.tt_ps.ui.viewmodel;

import androidx.recyclerview.widget.RecyclerView;

import com.firebase.geofire.GeoLocation;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;

import static es.udc.psi.tt_ps.domain.activity.getFirstActivitiesFiltered.getActivitiesFiltered;
import static es.udc.psi.tt_ps.domain.activity.getUserActivitiesUseCase.getActivitiesByAdmin;
import static es.udc.psi.tt_ps.domain.activity.getNextUserActiviesUseCase.getNextActivitiesByAdmin;


import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import es.udc.psi.tt_ps.data.model.ActivityModel;
import es.udc.psi.tt_ps.data.model.QueryResult;
import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.data.repository.activityRepository;
import es.udc.psi.tt_ps.ui.adapter.ListActivitiesAdapter;
import es.udc.psi.tt_ps.ui.fragments.ActivityListFragment;

public class SavedActivitiesListPres  extends RecyclerView.OnScrollListener{
    activityRepository ar = new activityRepository();
    private DocumentSnapshot prevDocSnap;
    public boolean getMore = true;
    public List<String> participants = new ArrayList<>();



    public void setRecycledDataByRol(String rol,RecyclerView recyclerView,String uid) throws InterruptedException {
        if(rol=="admin"){
            Result<QueryResult<List<ActivityModel>, DocumentSnapshot>, Exception> data;
            ListActivitiesAdapter adapter = (ListActivitiesAdapter) recyclerView.getAdapter();

            data = getActivitiesByAdmin(uid,10);

            if(data.data.data.size() == 0){
                Snackbar.make(recyclerView.getRootView(), "No hay actividades que coincidan con los filtros", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                adapter.setItems(new ArrayList<>());
            }else {
                if (data.exception == null && data.data.cursor != null) {
                    List<ActivityModel> res = new ArrayList<>(data.data.data);

                    prevDocSnap = data.data.cursor;

                    assert adapter != null;
                    List<ListActivities> listActivities = new ArrayList<>();

                    for (int i = 0; i < res.size(); i++) {
                        if (res.get(i).getParticipants()!=null){
                            participants=res.get(i).getParticipants();
                        }else{
                            participants=new ArrayList<>();
                        }
                        listActivities.add(new ListActivities(res.get(i).getId(),res.get(i).getImage(),res.get(i).getTitle(),res.get(i).getLocation(),res.get(i).getEnd_date(),
                                res.get(i).getDescription(),res.get(i).getStart_date(),res.get(i).getCreation_date(),res.get(i).getAdminId(),participants,
                                res.get(i).getTags()));

                    }
                    adapter.setItems(listActivities);
                }else{
                    getMore = false;
                }

            }
        }else if(rol == "assistant"){
            //TODO
        }
    }

    public void setRecycledDataByRol(List<ListActivities> listActivities,String uuid,String Rol) throws InterruptedException {

        if (Rol == "admin") {

            Result<QueryResult<List<ActivityModel>, DocumentSnapshot>, Exception> data;


            data = getActivitiesByAdmin(uuid,10);
            if (data.exception == null && data.data.data.size() > 0) {
                Log.d("_TAG", "Presenter " + " data not null");
                List<ActivityModel> res = new ArrayList<>(data.data.data);
                prevDocSnap = data.data.cursor;

                for (int i = 0; i < res.size(); i++) {
                    if (res.get(i).getParticipants() != null) {
                        participants = res.get(i).getParticipants();
                    } else {
                        participants = new ArrayList<>();
                    }
                    listActivities.add(new ListActivities(res.get(i).getId(), res.get(i).getImage(), res.get(i).getTitle(), res.get(i).getLocation(), res.get(i).getEnd_date(),
                            res.get(i).getDescription(), res.get(i).getStart_date(), res.get(i).getCreation_date(), res.get(i).getAdminId(), participants,
                            res.get(i).getTags()));

                }

            }
        }else if(Rol == "assistant"){
            //TODO
        }
    }

    public void updateRecycledDataByRol(RecyclerView recyclerView,String uid,String Rol) throws InterruptedException {
        if(Rol == "admin"){

            Result<QueryResult<List<ActivityModel>, DocumentSnapshot>, Exception> data;

            data = getNextActivitiesByAdmin(uid,10,prevDocSnap);
            if (data.exception == null && data.data.cursor != null) {
                Log.d("_TAG", "Presenter " + " data not null");
                List<ActivityModel> res = new ArrayList<>(data.data.data);
                prevDocSnap = data.data.cursor;
                ListActivitiesAdapter adapter = (ListActivitiesAdapter) recyclerView.getAdapter();

                assert adapter != null;
                List<ListActivities> listActivities = adapter.getmData();

                for (int i = 0; i < res.size(); i++) {
                    if (res.get(i).getParticipants()!=null){
                        participants=res.get(i).getParticipants();
                    }
                    listActivities.add(new ListActivities(res.get(i).getId(),res.get(i).getImage(),res.get(i).getTitle(),res.get(i).getLocation(),res.get(i).getEnd_date(),
                            res.get(i).getDescription(),res.get(i).getStart_date(),res.get(i).getCreation_date(),res.get(i).getAdminId(),participants,
                            res.get(i).getTags()));

                }
                adapter.setItems(listActivities);
            }else{
                getMore = false;
            }
        }
    }



}


