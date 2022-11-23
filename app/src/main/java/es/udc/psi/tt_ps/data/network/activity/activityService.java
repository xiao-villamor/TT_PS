package es.udc.psi.tt_ps.data.network.activity;

import android.util.Log;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import es.udc.psi.tt_ps.data.model.ActivityModel;

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

    public List<ActivityModel> getActivities() throws ExecutionException, InterruptedException, TimeoutException {
        List<ActivityModel> data = new ArrayList<>();
        Query ref = db.collection("Activities").orderBy("creation_date", Query.Direction.DESCENDING).limit(10);
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
