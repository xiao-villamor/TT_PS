package es.udc.psi.tt_ps.data.repository;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import es.udc.psi.tt_ps.data.model.ActivityModel;
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

    public List<ActivityModel> getActivitiesByAdminId(String adminId) throws ExecutionException, InterruptedException, TimeoutException {
        return api.getActivitiesByAdminId(adminId);
    }

    public List<ActivityModel> getActivities() throws ExecutionException, InterruptedException, TimeoutException {
        return api.getActivities();
    }

    public List<ActivityModel> getNextActivities() throws ExecutionException, InterruptedException, TimeoutException {
        return api.getNextActivities();
    }




}
