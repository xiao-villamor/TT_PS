package es.udc.psi.tt_ps.data.repository;

import android.util.Log;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import es.udc.psi.tt_ps.data.model.ActivityModel;
import es.udc.psi.tt_ps.data.model.UserModel;

public class activityRepository {
    private final FirebaseAuth mAuth;
    private final FirebaseFirestore db;
    private final FirebaseStorage storage;
    private ActivityModel activity = null;


    public activityRepository(FirebaseAuth mAuth, FirebaseFirestore db, FirebaseStorage storage) {
        this.mAuth = mAuth;
        this.db = db;
        this.storage = storage;
    }

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
        Log.d("TAG", activity.toString());
        return activity;
    }




}
