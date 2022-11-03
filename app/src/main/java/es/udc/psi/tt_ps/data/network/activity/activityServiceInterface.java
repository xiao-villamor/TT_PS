package es.udc.psi.tt_ps.data.network.activity;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import es.udc.psi.tt_ps.data.model.ActivityModel;

public interface activityServiceInterface {
    public void createActivity(ActivityModel activity);
    public void updateActivity(ActivityModel activity, String id);
    public void deleteActivity(String id);
    public ActivityModel getActivity(String id) throws ExecutionException, InterruptedException, TimeoutException;
    public List<ActivityModel> getActivitiesByAdminId(String adminId) throws ExecutionException, InterruptedException, TimeoutException;
    public List<ActivityModel> getActivities() throws ExecutionException, InterruptedException, TimeoutException;
    public List<ActivityModel> getNextActivities() throws ExecutionException, InterruptedException, TimeoutException;
}
