package es.udc.psi.tt_ps.domain.activity;

import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.util.Date;
import java.util.List;

import es.udc.psi.tt_ps.data.model.ActivityModel;
import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.data.model.UserModel;
import es.udc.psi.tt_ps.data.repository.activityRepository;


public class createActivityUseCase {

    public static Result<Object, Exception> createAcyivity (String title, String description, Date startDate,
                                                                  Date endDate, String adminId , List<String> interests) throws InterruptedException {

        final activityRepository repository = new activityRepository();
        Result<Object, Exception> res = new Result<>();

        ActivityModel activity = new ActivityModel(title, description, startDate, endDate, new Date(System.currentTimeMillis()),null, adminId,null, interests);
        Thread thread = new Thread(() -> {
            try{
                repository.createActivity(activity);
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
