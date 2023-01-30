package es.udc.psi.tt_ps.data.repository;

import com.firebase.geofire.GeoLocation;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import es.udc.psi.tt_ps.data.model.ActivityModel;
import es.udc.psi.tt_ps.data.model.QueryResult;
import es.udc.psi.tt_ps.data.network.activity.activityService;

public class activityRepository {

    private final activityService api = new activityService();


    public void createActivity(ActivityModel activity) throws ExecutionException, InterruptedException, TimeoutException {
        api.createActivity(activity);
    }

    public void updateActivity(ActivityModel activity, String id) throws InterruptedException {
        api.updateActivity(activity, id);
    }
    public void addParticipant(ActivityModel activity, String id){
        api.addParticipant(activity, id);
    }
    public void removeParticipant(ActivityModel activity,String id){
        api.removeParticipant(activity,id);
    }
    public void deleteActivity(String id) throws ExecutionException, InterruptedException, TimeoutException {
        api.deleteActivity(id);
    }
    public void finalizeActivity(String id) throws ExecutionException, InterruptedException, TimeoutException {
        api.finalizeActivity(id);
    }

    public QueryResult<ActivityModel,DocumentSnapshot> getActivity(String id) throws ExecutionException, InterruptedException, TimeoutException {
        return api.getActivity(id);
    }
    public QueryResult<List<ActivityModel>,DocumentSnapshot> getActivitiesByAdmin(String adminId)throws ExecutionException, InterruptedException, TimeoutException {
        return api.getActivitiesByAdmin(adminId);
    }
    public QueryResult<List<ActivityModel>,DocumentSnapshot> getActivitiesByAdminId(String adminId,int count) throws ExecutionException, InterruptedException, TimeoutException {
        return api.getActivitiesByAdminId(adminId,count);
    }

    public QueryResult<List<ActivityModel>,DocumentSnapshot> getNextActivitiesByAdminId(String adminId,int count,DocumentSnapshot prevDocSnaprec) throws ExecutionException, InterruptedException, TimeoutException {
        return api.getNextActivitiesByAdminId(adminId,count,prevDocSnaprec);
    }

    public QueryResult<List<ActivityModel>,DocumentSnapshot> getActivities() throws ExecutionException, InterruptedException, TimeoutException {
        return api.getActivities();
    }
    public QueryResult<List<ActivityModel>,DocumentSnapshot> getAssistantActivitiesById(String uuid,int count) throws ExecutionException, InterruptedException,TimeoutException {
        return api.getAssistantActivitiesById(uuid,count);
    }

    public QueryResult<List<ActivityModel>,DocumentSnapshot> getNextAssistantActivitiesById(String uuid,int count,DocumentSnapshot prevDocSnaprec) throws ExecutionException, InterruptedException,TimeoutException{
        return api.getNextAssistantActivitiesById(uuid,count,prevDocSnaprec);
    }


    public  QueryResult<List<ActivityModel>,DocumentSnapshot> getNextActivities(DocumentSnapshot prevDocSnap) throws ExecutionException, InterruptedException, TimeoutException {
        return api.getNextActivities(prevDocSnap);
    }

    public QueryResult<List<ActivityModel>,Boolean> getActivitiesFiltered(List<String> tags, List<Float> distanceRange, GeoLocation location) throws ExecutionException, InterruptedException, TimeoutException {
        return api.getActivitiesFiltered(tags,distanceRange,location);
    }

    public QueryResult<List<ActivityModel>,Boolean> getActivitiesFilteredNext() throws ExecutionException, InterruptedException, TimeoutException{
        return api.getActivitiesFilteredNext();
    }

    public QueryResult<List<ActivityModel>,DocumentSnapshot> getActivitiesFilteredNext(List<String> tags, List<Float> distanceRange,DocumentSnapshot prevDocSnaprec,GeoLocation location) throws ExecutionException, InterruptedException, TimeoutException{
        return api.getActivitiesFilteredNext(tags,distanceRange,prevDocSnaprec,location);
    }

    public String uploadActivityPic(String uuid, byte[] image) throws FileNotFoundException, ExecutionException, InterruptedException, TimeoutException{
        return api.uploadActivityPic(uuid, image);
    }



}
