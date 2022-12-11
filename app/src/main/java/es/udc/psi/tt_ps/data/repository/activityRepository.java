package es.udc.psi.tt_ps.data.repository;

import com.firebase.geofire.GeoLocation;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.FileNotFoundException;
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

    public QueryResult<ActivityModel,DocumentSnapshot> getActivity(String id) throws ExecutionException, InterruptedException, TimeoutException {
        return api.getActivity(id);
    }
    public QueryResult<List<ActivityModel>,List<DocumentSnapshot>> getActivitiesByAdmin(String adminId)throws ExecutionException, InterruptedException, TimeoutException {
        return api.getActivitiesByAdmin(adminId);
    }
    public QueryResult<List<ActivityModel>,List<DocumentSnapshot>> getActivitiesByAdminId(String adminId,int count) throws ExecutionException, InterruptedException, TimeoutException {
        return api.getActivitiesByAdminId(adminId,count);
    }

    public QueryResult<List<ActivityModel>,List<DocumentSnapshot>> getActivities() throws ExecutionException, InterruptedException, TimeoutException {
        return api.getActivities();
    }

    public  QueryResult<List<ActivityModel>,List<DocumentSnapshot>> getNextActivities(DocumentSnapshot prevDocSnap) throws ExecutionException, InterruptedException, TimeoutException {
        return api.getNextActivities(prevDocSnap);
    }

    public QueryResult<List<ActivityModel>,List<DocumentSnapshot>> getActivitiesFiltered(List<String> tags, List<Float> distanceRange, GeoLocation location) throws ExecutionException, InterruptedException, TimeoutException {
        return api.getActivitiesFiltered(tags,distanceRange,location);
    }
    public QueryResult<List<ActivityModel>,List<DocumentSnapshot>> getActivitiesFilteredNext(List<String> tags, List<Float> distanceRange,DocumentSnapshot prevDocSnaprec,GeoLocation location) throws ExecutionException, InterruptedException, TimeoutException{
        return api.getActivitiesFilteredNext(tags,distanceRange,prevDocSnaprec,location);
    }

    public String uploadActivityPic(String uuid, byte[] image) throws FileNotFoundException, ExecutionException, InterruptedException, TimeoutException{
        return api.uploadActivityPic(uuid, image);
    }

}
