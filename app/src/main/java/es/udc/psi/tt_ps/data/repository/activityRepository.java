package es.udc.psi.tt_ps.data.repository;

import android.util.Log;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import es.udc.psi.tt_ps.data.model.ActivityModel;

public class activityRepository {
    private final FirebaseAuth mAuth;
    private final FirebaseFirestore db;
    private ActivityModel activity = null;


    public activityRepository(FirebaseAuth mAuth, FirebaseFirestore db, FirebaseStorage storage) {
        this.mAuth = mAuth;
        this.db = db;
    }

    private DocumentSnapshot prevDocSnap = null;

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

    public List<ActivityModel> getActivitiesByAdminId(String adminId) throws ExecutionException, InterruptedException, TimeoutException {
        List<ActivityModel> data = new ArrayList<>();
        Query ref = db.collection("Activities").whereEqualTo("adminId", adminId).orderBy("creation_date", Query.Direction.DESCENDING);
        Tasks.await(ref.get(), 5, TimeUnit.SECONDS).getDocuments().forEach(document -> {
            if(document != null) {
                data.add(document.toObject(ActivityModel.class));
            }else{
                Log.d("TAG", "No such document");
            }
        });
        return data;
    }

    public List<ActivityModel> getActivities() throws ExecutionException, InterruptedException, TimeoutException {
        List<ActivityModel> data = new ArrayList<>();
        Query ref = db.collection("Activities").orderBy("creation_date", Query.Direction.DESCENDING).limit(5);
        Tasks.await(ref.get(), 15, TimeUnit.SECONDS).getDocuments().forEach(document -> {
            if(document != null) {
                data.add(document.toObject(ActivityModel.class));
                prevDocSnap = document;
            }else{
                Log.d("TAG", "No such document");
            }
        });

        return data;
    }

    public List<ActivityModel> getNextActivities() throws ExecutionException, InterruptedException, TimeoutException {
        List<ActivityModel> data = new ArrayList<>();
        Query ref = db.collection("Activities").orderBy("creation_date", Query.Direction.DESCENDING).startAfter(prevDocSnap).limit(5);
        Tasks.await(ref.get(), 15, TimeUnit.SECONDS).getDocuments().forEach(document -> {
            if(document != null) {
                data.add(document.toObject(ActivityModel.class));
            }else{
                Log.d("TAG", "No such document");
            }
        });
        return data;
    }




}
