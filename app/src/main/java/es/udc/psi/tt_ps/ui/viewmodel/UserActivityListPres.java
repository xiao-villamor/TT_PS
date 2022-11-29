package es.udc.psi.tt_ps.ui.viewmodel;

import static es.udc.psi.tt_ps.domain.activity.getUserActivitiesUseCase.getActivitiesByAdmin;


import android.graphics.PointF;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

import es.udc.psi.tt_ps.core.firebaseConnection;
import es.udc.psi.tt_ps.data.model.ActivityModel;
import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.data.repository.activityRepository;


public class UserActivityListPres {

    activityRepository ar = new activityRepository();
    public List<String> participants = new ArrayList<>();


    public void setRecycledData(List<ListActivities> listActivities) throws InterruptedException {
        Log.d("_TAG","Presenter "+" start init");

        Result<List<ActivityModel>, Exception> data ;

        data = getActivitiesByAdmin(firebaseConnection.getUser(),5);

        List<ActivityModel> res = new ArrayList<>(data.data);

        for (int i=0; i<res.size();i++){
            if (res.get(i).getParticipants()!=null){
                participants=res.get(i).getParticipants();
            }
            listActivities.add(new ListActivities(res.get(i).getImage(),res.get(i).getTitle(),res.get(i).getLocation(),res.get(i).getEnd_date(),
                    res.get(i).getDescription(),res.get(i).getStart_date(),res.get(i).getCreation_date(),res.get(i).getAdminId(),participants,
                    res.get(i).getTags()));
        }
    }


}
