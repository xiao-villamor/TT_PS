package es.udc.psi.tt_ps.domain.activity;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;

import es.udc.psi.tt_ps.data.model.ActivityModel;
import es.udc.psi.tt_ps.data.model.QueryResult;
import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.data.repository.activityRepository;

public class unsubscribeActivityUseCase {
    public static Result<QueryResult<ActivityModel, DocumentSnapshot>,Exception> unsubscribeActivity(String activityId, String UserId) throws InterruptedException {

        final activityRepository repository = new activityRepository();
        Result<QueryResult<ActivityModel,DocumentSnapshot>, Exception> result = new Result<>();


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
            result.data.data.getParticipants().remove(UserId);
        }else{
            Log.d("TAG","No se ha encontrado la actividad");
            return result;
        }

        Thread thread2 = new Thread(() -> {
            try{
                repository.removeParticipant(result.data.data,activityId);
            }catch (Exception e){
                result.exception = e;
            }
        });
        thread2.start();
        thread2.join();
        thread2.interrupt();
        return result;
    }
}
