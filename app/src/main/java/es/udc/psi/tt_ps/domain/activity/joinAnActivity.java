package es.udc.psi.tt_ps.domain.activity;



import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import es.udc.psi.tt_ps.data.model.ActivityModel;
import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.data.repository.activityRepository;
import es.udc.psi.tt_ps.ui.viewmodel.ListActivities;

public class joinAnActivity {
    public static Result<List<ActivityModel>, Exception> joinAnActivity(ListActivities listActivities) throws InterruptedException {

        final activityRepository repository = new activityRepository();
        Result<List<ActivityModel>, Exception> res = new Result<>();
        ActivityModel activity = new ActivityModel();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String currentUserId= user.getUid();
        Date date = listActivities.creation_date;


        Thread thread1 = new Thread(() -> {
            try {
                res.data = repository.getActivitiesByAdmin(listActivities.getAdminId());
            } catch (Exception e) {
                res.exception = e;
            }
        });
        thread1.start();
        thread1.join();
        thread1.interrupt();

        for(ActivityModel i: res.data){
            if(i.getCreation_date().equals(date)) {
                if(i.getParticipants()==null){
                    List<String> listaParticipantes = new ArrayList<>();
                    listaParticipantes.add(currentUserId);
                    activity = new ActivityModel(i.getTitle(), i.getDescription(), i.getCreation_date(), i.getEnd_date(),
                            i.getCreation_date(), i.getLocation(), i.getAdminId(), listaParticipantes, i.getTags(),
                            i.getImage(), i.getGeohash());
                }else {
                    i.getParticipants().add(currentUserId);
                    activity = new ActivityModel(i.getTitle(), i.getDescription(), i.getCreation_date(), i.getEnd_date(),
                            i.getCreation_date(), i.getLocation(), i.getAdminId(), i.getParticipants(), i.getTags(),
                            i.getImage(), i.getGeohash());
                }


            }
        }


        /*Thread thread2 = new Thread(() -> {
            try{
                repository.createActivity(activity);
            }catch (Exception e){
                res.exception = e;
            }
        });
        thread2.start();
        thread2.join();
        thread2.interrupt();*/


        return res;
        }


    }

