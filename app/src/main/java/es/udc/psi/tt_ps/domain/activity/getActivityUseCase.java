package es.udc.psi.tt_ps.domain.activity;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import es.udc.psi.tt_ps.data.model.ActivityModel;
import es.udc.psi.tt_ps.data.model.QueryResult;
import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.data.repository.activityRepository;

public class getActivityUseCase {
    public static Result<ActivityModel, Exception> getActivityUseCase(String uuid) throws InterruptedException {


        final activityRepository repository = new activityRepository();
        Result<ActivityModel, Exception> res = new Result<>();
        Thread thread = new Thread(() -> {
            try{
                res.data = repository.getActivity(uuid).data;
            }catch (Exception e){
                res.exception = e;
            }
        });
        thread.start();
        thread.join();
        thread.interrupt();

        return res;
    }

}
