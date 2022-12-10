package es.udc.psi.tt_ps.domain.activity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import es.udc.psi.tt_ps.data.model.ActivityModel;
import es.udc.psi.tt_ps.data.model.QueryResult;
import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.data.repository.activityRepository;
import es.udc.psi.tt_ps.ui.viewmodel.ListActivities;

public class joinAnActivity {
    public static Result<QueryResult<ActivityModel, DocumentSnapshot>,Exception> joinAnActivity(String activityId) throws InterruptedException {

        final activityRepository repository = new activityRepository();
        //Result<ActivityModel, Exception> res = new Result<>();
        Result<QueryResult<ActivityModel,DocumentSnapshot>, Exception> result = new Result<>();


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String currentUserId= user.getUid();

        //res.data=null;
        Thread thread1 = new Thread(() -> {
            try {
                result.data=repository.getActivity(activityId);
            } catch (Exception e) {
                result.exception = e;
            }
        });
        thread1.start();
        thread1.join();
        thread1.interrupt();

        if (result.data.data!=null){
            joinUser(result.data.data,currentUserId);
        }else{
            Log.d("TAG","No se ha encontrado la actividad");
            return result;
        }

        Thread thread2 = new Thread(() -> {
            try{
                repository.updateActivity(result.data.data,activityId);
            }catch (Exception e){
                result.exception = e;
            }
        });
        thread2.start();
        thread2.join();
        thread2.interrupt();
        return result;
    }


    public static void joinUser(ActivityModel activityModel, String userId){

        if(activityModel.getParticipants()!=null){
            activityModel.getParticipants().add(userId);
        }else{
            List<String> listaParticipantes = new ArrayList<>();
            listaParticipantes.add(userId);
            activityModel.setParticipants(listaParticipantes);
        }

    }

}





