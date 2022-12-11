package es.udc.psi.tt_ps.domain.activity;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import es.udc.psi.tt_ps.data.model.ActivityModel;
import es.udc.psi.tt_ps.data.model.QueryResult;
import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.data.repository.activityRepository;
import es.udc.psi.tt_ps.ui.viewmodel.ListActivities;

public class editActivityInfoUseCase {

    public static Result<Object, Exception> updateEditedActivity(ListActivities act) throws InterruptedException {

        final activityRepository repository = new activityRepository();
        Result<Object, Exception> res = new Result<>();

        String hash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(act.getLocation().getLatitude(), act.getLocation().getLongitude()));
        ActivityModel activity = new ActivityModel(act.getTitle(), act.getDescription(), act.start_date, act.end_date,
                act.creation_date, act.location, act.adminId, act.getParticipants(), act.getTags(), act.getActivityImage(), hash);
        Thread thread1 = new Thread(() -> {
            try {
                repository.updateActivity(activity, act.getActivityId());
            } catch (Exception e) {
                res.exception = e;
            }
        });
        thread1.start();
        thread1.join();
        thread1.interrupt();



        return res;
    }


}
