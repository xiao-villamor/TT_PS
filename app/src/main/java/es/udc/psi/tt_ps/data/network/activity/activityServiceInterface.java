package es.udc.psi.tt_ps.data.network.activity;

import com.firebase.geofire.GeoLocation;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import es.udc.psi.tt_ps.data.model.ActivityModel;
import es.udc.psi.tt_ps.data.model.QueryResult;

public interface activityServiceInterface {
    public void createActivity(ActivityModel activity);
    public void updateActivity(ActivityModel activity, String id);
    public void deleteActivity(String id);
    public QueryResult<ActivityModel,DocumentSnapshot> getActivity(String id) throws ExecutionException, InterruptedException, TimeoutException;
    public QueryResult<List<ActivityModel>,List<DocumentSnapshot>> getActivitiesByAdminId(String adminId,int count) throws ExecutionException, InterruptedException, TimeoutException;
    public QueryResult<List<ActivityModel>,List<DocumentSnapshot>> getActivities() throws ExecutionException, InterruptedException, TimeoutException;
    public QueryResult<List<ActivityModel>,List<DocumentSnapshot>> getActivitiesFiltered(List<String> tags, List<Float> distanceRange, GeoLocation location);
    public QueryResult<List<ActivityModel>,List<DocumentSnapshot>> getNextActivities(DocumentSnapshot prevDocSnap) throws ExecutionException, InterruptedException, TimeoutException;
}
