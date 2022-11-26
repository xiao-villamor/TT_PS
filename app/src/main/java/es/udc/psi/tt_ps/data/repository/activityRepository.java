package es.udc.psi.tt_ps.data.repository;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import es.udc.psi.tt_ps.data.model.ActivityModel;
import es.udc.psi.tt_ps.data.model.QueryResult;
import es.udc.psi.tt_ps.data.network.activity.activityService;

public class activityRepository {

    private final activityService api = new activityService();


    public void createActivity(ActivityModel activity){
        api.createActivity(activity);
    }

    public void updateActivity(ActivityModel activity, String id){
        api.updateActivity(activity, id);
    }

    public void deleteActivity(String id){
        api.deleteActivity(id);
    }

    public ActivityModel getActivity(String id) throws ExecutionException, InterruptedException, TimeoutException {
        return api.getActivity(id);
    }

    public List<ActivityModel> getActivitiesByAdminId(String adminId,int count) throws ExecutionException, InterruptedException, TimeoutException {
        return api.getActivitiesByAdminId(adminId,count);
    }

    public QueryResult<List<ActivityModel>,DocumentSnapshot> getActivities() throws ExecutionException, InterruptedException, TimeoutException {
        return api.getActivities();
    }

    public  QueryResult<List<ActivityModel>,DocumentSnapshot> getNextActivities(DocumentSnapshot prevDocSnap) throws ExecutionException, InterruptedException, TimeoutException {
        return api.getNextActivities(prevDocSnap);
    }




}
