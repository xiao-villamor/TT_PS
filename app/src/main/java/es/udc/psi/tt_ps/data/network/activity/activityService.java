package es.udc.psi.tt_ps.data.network.activity;

import android.util.Log;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import es.udc.psi.tt_ps.data.model.ActivityModel;
import es.udc.psi.tt_ps.data.model.QueryResult;

public class activityService implements activityServiceInterface {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
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

    public ActivityModel getActivity(String id) throws ExecutionException, InterruptedException, TimeoutException {
        DocumentReference userDocument = (db.collection("Activities").document(id));

        DocumentSnapshot res =  Tasks.await(userDocument.get(), 5, TimeUnit.SECONDS);
        if(res.exists()) {
            activity = res.toObject(ActivityModel.class);
        }
        return activity;
    }

    public List<ActivityModel> getActivitiesByAdminId(String adminId,int count) throws ExecutionException, InterruptedException, TimeoutException {
        List<ActivityModel> data = new ArrayList<>();
        Query ref = db.collection("Activities").whereEqualTo("adminId", adminId).orderBy("creation_date", Query.Direction.DESCENDING).limit(count);
        Tasks.await(ref.get(), 5, TimeUnit.SECONDS).getDocuments().forEach(document -> {
            if(document != null) {
                data.add(document.toObject(ActivityModel.class));
            }else{
                Log.d("TAG", "No such document");
            }
        });
        return data;
    }

    public QueryResult<List<ActivityModel>,DocumentSnapshot> getActivities() throws ExecutionException, InterruptedException, TimeoutException {
        List<ActivityModel> data = new ArrayList<>();
        QueryResult<List<ActivityModel>,DocumentSnapshot> result = new QueryResult<>();

        Query ref = db.collection("Activities").orderBy("creation_date", Query.Direction.DESCENDING).limit(5);
        Tasks.await(ref.get(), 15, TimeUnit.SECONDS).getDocuments().forEach(document -> {
            if(document != null) {
                data.add(document.toObject(ActivityModel.class));
                prevDocSnap = document;
            }else{
                Log.d("TAG", "No such document");
            }
        });

        Log.d("_TAG", "DocumentSnapshot data: " + prevDocSnap);
        result.data = data;
        result.cursor = prevDocSnap;

        return result;
    }

    @Override
    public QueryResult<List<ActivityModel>, DocumentSnapshot> getActivitiesFiltered(List<String> tags, List<Float> distanceRange) throws ExecutionException, InterruptedException, TimeoutException {
        List<ActivityModel> data = new ArrayList<>();
        QueryResult<List<ActivityModel>,DocumentSnapshot> result = new QueryResult<>();
        Query ref = db.collection("Activities").orderBy("creation_date", Query.Direction.DESCENDING).limit(5).whereArrayContainsAny("tags", tags);
        Tasks.await(ref.get(), 15, TimeUnit.SECONDS).getDocuments().forEach(document -> {
            if(document != null) {
                Log.d("_TAG", "DocumentSnapshot data: " + document.toObject(ActivityModel.class));

                data.add(document.toObject(ActivityModel.class));
                prevDocSnap = document;
            }else{
                Log.d("TAG", "No such document");
            }
        });



        result.data = data;
        result.cursor = prevDocSnap;

        return result;
    }

    public QueryResult<List<ActivityModel>, DocumentSnapshot> getActivitiesFilteredNext(List<String> tags, List<Float> distanceRange,DocumentSnapshot prevDocSnaprec) throws ExecutionException, InterruptedException, TimeoutException {
        List<ActivityModel> data = new ArrayList<>();
        QueryResult<List<ActivityModel>,DocumentSnapshot> result = new QueryResult<>();
        Query ref = db.collection("Activities").orderBy("creation_date", Query.Direction.DESCENDING).startAfter(prevDocSnaprec).limit(5).whereArrayContainsAny("tags", tags);
        Tasks.await(ref.get(), 15, TimeUnit.SECONDS).getDocuments().forEach(document -> {
            if(document != null) {
                Log.d("_TAG", "DocumentSnapshot data: " + document.toObject(ActivityModel.class));

                data.add(document.toObject(ActivityModel.class));
                prevDocSnap = document;
            }else{
                Log.d("TAG", "No such document");
            }
        });



        result.data = data;
        result.cursor = prevDocSnap;

        return result;
    }

    public QueryResult<List<ActivityModel>,DocumentSnapshot>  getNextActivities(DocumentSnapshot prevDocSnaprec) throws ExecutionException, InterruptedException, TimeoutException {
        Log.d("_TAG", "POINTER data: " + prevDocSnap);

        List<ActivityModel> data = new ArrayList<>();
        QueryResult<List<ActivityModel>,DocumentSnapshot> result = new QueryResult<>();


        Query ref = db.collection("Activities").orderBy("creation_date", Query.Direction.DESCENDING).startAfter(prevDocSnaprec).limit(5);
        Tasks.await(ref.get(), 15, TimeUnit.SECONDS).getDocuments().forEach(document -> {
            if(document != null) {
                data.add(document.toObject(ActivityModel.class));
                prevDocSnap = document;
            }else{
                Log.d("TAG", "No such document");
            }
        });
        result.data = data;
        result.cursor = prevDocSnap;

        return result;
    }



}
