package es.udc.psi.tt_ps.data.network.activity;

import com.firebase.geofire.GeoLocation;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import es.udc.psi.tt_ps.data.model.ActivityModel;
import es.udc.psi.tt_ps.data.model.QueryResult;

public interface activityServiceInterface {
    public void createActivity(ActivityModel activity) throws ExecutionException, InterruptedException, TimeoutException;
    public void updateActivity(ActivityModel activity, String id) throws InterruptedException;
    public void deleteActivity(String id) throws ExecutionException, InterruptedException, TimeoutException;
    public QueryResult<ActivityModel,DocumentSnapshot> getActivity(String id) throws ExecutionException, InterruptedException, TimeoutException;
    public QueryResult<List<ActivityModel>,DocumentSnapshot> getActivitiesByAdminId(String adminId,int count) throws ExecutionException, InterruptedException, TimeoutException;
    public QueryResult<List<ActivityModel>,DocumentSnapshot> getNextActivitiesByAdminId(String adminId,int count,DocumentSnapshot prevDocSnaprec) throws ExecutionException, InterruptedException, TimeoutException;
    public QueryResult<List<ActivityModel>,DocumentSnapshot> getAssistantActivitiesById(String uuid,int count) throws ExecutionException, InterruptedException,TimeoutException;
    public QueryResult<List<ActivityModel>,DocumentSnapshot> getNextAssistantActivitiesById(String uuid,int count,DocumentSnapshot prevDocSnaprec) throws ExecutionException, InterruptedException,TimeoutException;
    public QueryResult<List<ActivityModel>,DocumentSnapshot> getActivities() throws ExecutionException, InterruptedException, TimeoutException;
    public QueryResult<List<ActivityModel>,Boolean> getActivitiesFiltered(List<String> tags, List<Float> distanceRange, GeoLocation location);
    public QueryResult<List<ActivityModel>,DocumentSnapshot> getNextActivities(DocumentSnapshot prevDocSnap) throws ExecutionException, InterruptedException, TimeoutException;
    public String uploadActivityPic(String uuid, byte[] image) throws FileNotFoundException, ExecutionException, InterruptedException, TimeoutException;
}
