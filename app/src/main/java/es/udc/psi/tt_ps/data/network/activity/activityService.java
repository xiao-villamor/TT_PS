package es.udc.psi.tt_ps.data.network.activity;

import android.util.Log;

import androidx.annotation.NonNull;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryBounds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import es.udc.psi.tt_ps.data.model.ActivityModel;
import es.udc.psi.tt_ps.data.model.QueryResult;

public class activityService implements activityServiceInterface {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private DocumentSnapshot prevDocSnap = null;
    private ActivityModel activity = null;


    public void createActivity(ActivityModel activity){
        db.collection("Activities").document().set(activity);
    }

    public void updateActivity(ActivityModel activity, String id){
        db.collection("Activities").document(id).set(activity);
    }

    public void deleteActivity(String id){
        db.collection("Activities").document(id).delete();
    }

    public QueryResult<ActivityModel,DocumentSnapshot> getActivity(String id) throws ExecutionException, InterruptedException, TimeoutException {
        DocumentReference userDocument = (db.collection("Activities").document(id));
        QueryResult<ActivityModel,DocumentSnapshot> result = new QueryResult<>();

        DocumentSnapshot res =  Tasks.await(userDocument.get(), 5, TimeUnit.SECONDS);
        if(res.exists()) {
            result.data = res.toObject(ActivityModel.class);
            result.cursor = res;
        }
        return result;
    }

    public QueryResult<List<ActivityModel>,DocumentSnapshot> getActivitiesByAdmin(String adminId) throws ExecutionException, InterruptedException, TimeoutException {
        List<ActivityModel> data = new ArrayList<>();
        DocumentSnapshot doc;
        QueryResult<List<ActivityModel>,DocumentSnapshot> result = new QueryResult<>();
        Query ref = db.collection("Activities").whereEqualTo("adminId", adminId);
        Tasks.await(ref.get(), 5, TimeUnit.SECONDS).getDocuments().forEach(document -> {
            if(document != null) {
                    ActivityModel activity = document.toObject(ActivityModel.class);
                    activity.setId(document.getId());
                    data.add(activity);
                    prevDocSnap = document;
            }else{
                Log.d("TAG", "No such document");
            }
        });
        result.data=data;
        result.cursor=prevDocSnap;
        return result;
    }


    public QueryResult<List<ActivityModel>,DocumentSnapshot> getActivitiesByAdminId(String adminId,int count) throws ExecutionException, InterruptedException, TimeoutException {
        List<ActivityModel> data = new ArrayList<>();

        QueryResult<List<ActivityModel>,DocumentSnapshot> result = new QueryResult<>();


        Query ref = db.collection("Activities").whereEqualTo("adminId", adminId).orderBy("creation_date", Query.Direction.DESCENDING).limit(count);
        Tasks.await(ref.get(), 5, TimeUnit.SECONDS).getDocuments().forEach(document -> {
            if(document != null) {
                ActivityModel activity = document.toObject(ActivityModel.class);
                activity.setId(document.getId());
                data.add(activity);
                prevDocSnap = document;
            }else{
                Log.d("TAG", "No such document");
            }
        });
        result.data=data;
        result.cursor=prevDocSnap;
        return result;
    }

    public QueryResult<List<ActivityModel>,DocumentSnapshot> getNextActivitiesByAdminId(String adminId,int count,DocumentSnapshot prevDocSnaprec) throws ExecutionException, InterruptedException, TimeoutException {
        List<ActivityModel> data = new ArrayList<>();
        QueryResult<List<ActivityModel>,DocumentSnapshot> result = new QueryResult<>();

        Query ref = db.collection("Activities").whereEqualTo("adminId", adminId).orderBy("creation_date", Query.Direction.DESCENDING).startAfter(prevDocSnaprec).limit(count);
        Tasks.await(ref.get(), 5, TimeUnit.SECONDS).getDocuments().forEach(document -> {
            if(document != null) {
                ActivityModel activity = document.toObject(ActivityModel.class);
                activity.setId(document.getId());
                data.add(activity);
                prevDocSnap = document;
            }else{
                Log.d("TAG", "No such document");
            }
        });
        
        result.data=data;
        result.cursor=prevDocSnap;

        return result;
    }


    public QueryResult<List<ActivityModel>, DocumentSnapshot> getActivities() throws ExecutionException, InterruptedException, TimeoutException {
        List<ActivityModel> data = new ArrayList<>();
        QueryResult<List<ActivityModel>,DocumentSnapshot> result = new QueryResult<>();

        Query ref = db.collection("Activities").orderBy("creation_date", Query.Direction.DESCENDING).limit(5);
        Tasks.await(ref.get(), 15, TimeUnit.SECONDS).getDocuments().forEach(document -> {
            if(document != null) {
                ActivityModel activity = document.toObject(ActivityModel.class);
                activity.setId(document.getId());
                data.add(activity);
                prevDocSnap = document;
            }else{
                Log.d("TAG", "No such document");
            }
        });

        result.data=data;
        result.cursor=prevDocSnap;
        return result;
    }

    public QueryResult<List<ActivityModel>,DocumentSnapshot> getActivitiesFiltered(List<String> tags, List<Float> distanceRange, GeoLocation location) {
        List<ActivityModel> data = new ArrayList<>();
        final double radiusInM = 1000 * distanceRange.get(1);
        QueryResult<List<ActivityModel>,DocumentSnapshot> result = new QueryResult<>();


        List<GeoQueryBounds> bounds = GeoFireUtils.getGeoHashQueryBounds(location, radiusInM);
        final List<Task<QuerySnapshot>> tasks = new ArrayList<>();
        for (GeoQueryBounds b : bounds) {
            Query q = db.collection("Activities")
                    .orderBy("geohash")
                    .orderBy("creation_date", Query.Direction.DESCENDING)
                    .startAt(b.startHash)
                    .endAt(b.endHash)
                    .whereArrayContainsAny("tags", tags)
                    .limit(10);
            tasks.add(q.get());
        }

        tasks.forEach(task -> {
            try {
                Tasks.await(task, 15, TimeUnit.SECONDS).getDocuments().forEach(document -> {
                    if(document != null) {
                        GeoLocation docLocation = new GeoLocation(document.getGeoPoint("location").getLatitude(), document.getGeoPoint("location").getLongitude());

                        double distanceInKm = GeoFireUtils.getDistanceBetween(docLocation,location);
                        distanceInKm = distanceInKm/1000;

                        if(distanceInKm >= distanceRange.get(0) && distanceInKm <= distanceRange.get(1)) {
                            prevDocSnap = document;
                            ActivityModel activity = document.toObject(ActivityModel.class);
                            activity.setId(document.getId());
                            data.add(activity);

                        }
                    }else{
                        Log.d("_TAG", "No such document");
                    }
                });
            } catch (ExecutionException | InterruptedException | TimeoutException e) {
                e.printStackTrace();
            }
        });

        result.data=data;
        result.cursor = prevDocSnap;

        return result;
    }

    public QueryResult<List<ActivityModel>,DocumentSnapshot> getActivitiesFilteredNext(List<String> tags, List<Float> distanceRange,DocumentSnapshot prevDocSnaprec,GeoLocation location) throws ExecutionException, InterruptedException, TimeoutException {
        List<ActivityModel> data = new ArrayList<>();
        final double radiusInM = 1000 * distanceRange.get(1);

        QueryResult<List<ActivityModel>,DocumentSnapshot> result = new QueryResult<>();

        List<GeoQueryBounds> bounds = GeoFireUtils.getGeoHashQueryBounds(location, radiusInM);


        final List<Task<QuerySnapshot>> tasks = new ArrayList<>();
        for (GeoQueryBounds b : bounds) {
            Query q = db.collection("Activities")
                    .orderBy("geohash")
                    .orderBy("creation_date", Query.Direction.DESCENDING)
                    .startAt(b.startHash)
                    .endAt(b.endHash)
                    .whereArrayContainsAny("tags", tags)
                    .startAfter(prevDocSnaprec)
                    .limit(10);

            tasks.add(q.get());
        }

        tasks.forEach(task -> {
            try {
                Tasks.await(task, 15, TimeUnit.SECONDS).getDocuments().forEach(document -> {
                    if(document != null) {
                        GeoLocation docLocation = new GeoLocation(document.getGeoPoint("location").getLatitude(), document.getGeoPoint("location").getLongitude());

                        double distanceInKm = GeoFireUtils.getDistanceBetween(docLocation,location);
                        distanceInKm = distanceInKm/1000;

                        if(distanceInKm >= distanceRange.get(0) && distanceInKm <= distanceRange.get(1)) {
                            prevDocSnap = document;
                            ActivityModel activity = document.toObject(ActivityModel.class);
                            activity.setId(document.getId());
                            Log.d("_TAG", "getActivitiesFilteredNext: "+activity.getTitle());
                            data.add(activity);
                        }
                    }else{
                        Log.d("TAG", "No such document");
                    }
                });
            } catch (ExecutionException | InterruptedException | TimeoutException e) {
                e.printStackTrace();
            }
        });


        result.data = data;
        result.cursor = prevDocSnap;
        return result;
    }

    public QueryResult<List<ActivityModel>,DocumentSnapshot>  getNextActivities(DocumentSnapshot prevDocSnaprec) throws ExecutionException, InterruptedException, TimeoutException {
        List<ActivityModel> data = new ArrayList<>();
        QueryResult<List<ActivityModel>,DocumentSnapshot> result = new QueryResult<>();


        Query ref = db.collection("Activities").orderBy("creation_date", Query.Direction.DESCENDING).startAfter(prevDocSnaprec).limit(5);
        Tasks.await(ref.get(), 15, TimeUnit.SECONDS).getDocuments().forEach(document -> {
            if(document != null) {
                ActivityModel activity = document.toObject(ActivityModel.class);
                activity.setId(document.getId());
                data.add(activity);
                prevDocSnap = document;
            }else{
                Log.d("TAG", "No such document");
            }
        });
        result.data = data;
        result.cursor = prevDocSnaprec;

        return result;
    }


    public String uploadActivityPic(String uuid, byte[] image) throws FileNotFoundException, ExecutionException, InterruptedException, TimeoutException {

        StorageReference profilePicRef = storage.getReference("activity_image/"+uuid+".jpg");
        String uri = null;
        UploadTask.TaskSnapshot res=Tasks.await(profilePicRef.putBytes(image)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d("TAG", "Imagen subida");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", "Imagen NO subida");
                    }
                }), 10, TimeUnit.SECONDS);

        if(res != null){
            uri = Tasks.await(profilePicRef.getDownloadUrl(), 10, TimeUnit.SECONDS).toString();
            Log.d("TAG",uri);
        }else{
            uri=null;
        }

        return uri;
    }

}
