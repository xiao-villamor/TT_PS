package es.udc.psi.tt_ps.data.network.activity;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import es.udc.psi.tt_ps.data.model.ActivityModel;
import es.udc.psi.tt_ps.data.model.QueryResult;
import es.udc.psi.tt_ps.data.network.API.ApiClient;

public class activityService implements activityServiceInterface {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private DocumentSnapshot prevDocSnap = null;
    private ActivityModel activity = null;

    ApiClient apiClient = new ApiClient();



    public void createActivity(ActivityModel activity) throws ExecutionException, InterruptedException, TimeoutException {
        //get the id of the document created
        //Tasks.await(db.collection("Activities").document().set(activity), 45, TimeUnit.SECONDS);
        db.collection("Activities").add(activity).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("_TAG","Subscribing to activity: "+documentReference.getId());
                        activity.setId(documentReference.getId());
                        try {
                            updateActivity(activity,documentReference.getId());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    public void updateActivity(ActivityModel activity, String id) throws InterruptedException {
        db.collection("Activities").document(id).set(activity);
        //send message to topic ussing firebase messaging
        apiClient.NotifyUpdate(id,activity.getTitle());

    }

    public void addParticipant(ActivityModel activity,String id){
        db.collection("Activities").document(id).set(activity);
        subscribeToActivity(id);
    }

    public void removeParticipant(ActivityModel activity,String id){
        db.collection("Activities").document(id).set(activity);
        unsubscribeToActivity(id);
    }

    public void subscribeToActivity(String id){
        Log.d("_TAG", "Subscribing to activity: "+id);
        FirebaseMessaging.getInstance().subscribeToTopic(id)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed";
                        if (!task.isSuccessful()) {
                            msg = "Subscribe failed";
                        }
                        Log.d("TAG", msg);
                    }
                });
    }

    public void unsubscribeToActivity(String uuid){
        FirebaseMessaging.getInstance().unsubscribeFromTopic(uuid)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Unsubscribed";
                        if (!task.isSuccessful()) {
                            msg = "Unsubscribe failed";
                        }
                        Log.d("TAG", msg);
                    }
                });
    }

    public void deleteActivity(String id) throws ExecutionException, InterruptedException, TimeoutException {
        QueryResult<ActivityModel,DocumentSnapshot> activity = getActivity(id);
        db.collection("Activities").document(id).delete();
        unsubscribeToActivity(id);
        Log.d("_TAG", "Unsubscribing to activity: "+id);
        apiClient.NotifyDelete(id,activity.data.getTitle());

    }

    public void finalizeActivity(String id) throws ExecutionException, InterruptedException, TimeoutException {
        QueryResult<ActivityModel,DocumentSnapshot> activity = getActivity(id);
        unsubscribeToActivity(id);
        db.collection("Activities").document(id).delete();
        Log.d("_TAG", "Unsubscribing to activity: "+id);
        List<String> participants = activity.data.getParticipants();
        //count number of participants in the List ussing the size of the list
        apiClient.NotifyRate(id,activity.data.getTitle(),activity.data.getAdminId(),participants.size());

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

    public QueryResult<List<ActivityModel>,DocumentSnapshot> getAssistantActivitiesById(String uuid,int count) throws ExecutionException, InterruptedException,TimeoutException{

        List<ActivityModel> data = new ArrayList<>();
        QueryResult<List<ActivityModel>,DocumentSnapshot> result = new QueryResult<>();
        Log.d("TAG", "GetAssist Activities: "+uuid);

        Query ref = db.collection("Activities").whereArrayContains("participants", uuid).orderBy("start_date", Query.Direction.DESCENDING).limit(count);
        Tasks.await(ref.get(), 30, TimeUnit.SECONDS).getDocuments().forEach(document -> {
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
        Tasks.await(ref.get(), 30, TimeUnit.SECONDS).getDocuments().forEach(document -> {
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

    public QueryResult<List<ActivityModel>,DocumentSnapshot> getNextAssistantActivitiesById(String uuid,int count,DocumentSnapshot prevDocSnaprec) throws ExecutionException, InterruptedException,TimeoutException{

        List<ActivityModel> data = new ArrayList<>();
        QueryResult<List<ActivityModel>,DocumentSnapshot> result = new QueryResult<>();

        Query ref = db.collection("Activities").whereArrayContains("participants", uuid).orderBy("start_date", Query.Direction.DESCENDING).startAfter(prevDocSnaprec).limit(count);
        Tasks.await(ref.get(), 30, TimeUnit.SECONDS).getDocuments().forEach(document -> {
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
        Tasks.await(ref.get(), 30, TimeUnit.SECONDS).getDocuments().forEach(document -> {
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
        Tasks.await(ref.get(), 30, TimeUnit.SECONDS).getDocuments().forEach(document -> {
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

    public void ActivityListener(String uuid, Consumer<String> listener){
        Query ref = db.collection("Activities").whereArrayContains("participants", uuid);
        ref.addSnapshotListener(new EventListener<QuerySnapshot>() {

            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case MODIFIED:
                            listener.accept(dc.getDocument().getString("title") + " Modified");
                            break;
                        case REMOVED:
                            Log.d("TAG", "Removed city: " + dc.getDocument().getData());
                            listener.accept(dc.getDocument().getString("title") + " Deleted");
                            break;
                    }
                }

            }
        });
    }
}
