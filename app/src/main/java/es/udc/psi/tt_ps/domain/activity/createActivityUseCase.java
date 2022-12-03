package es.udc.psi.tt_ps.domain.activity;


import android.util.Log;

import com.google.firebase.firestore.GeoPoint;


import java.util.Date;
import java.util.List;

import es.udc.psi.tt_ps.data.model.ActivityModel;
import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.data.repository.activityRepository;


public class createActivityUseCase {

    public static Result<Object, Exception> createActivity(String title, String description, Date startDate,
                                                           Date endDate, String adminId , GeoPoint location, String geohash, List<String> interests) throws InterruptedException {

        final activityRepository repository = new activityRepository();
        Result<Object, Exception> res = new Result<>();

        Log.d("TAG",geohash);


        ActivityModel activity = new ActivityModel(title, description, startDate, endDate, new Date(System.currentTimeMillis()),location,adminId,null, interests, "",geohash);
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
