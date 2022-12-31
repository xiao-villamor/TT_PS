package es.udc.psi.tt_ps.domain.activity;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import es.udc.psi.tt_ps.data.model.ActivityModel;
import es.udc.psi.tt_ps.data.model.QueryResult;
import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.data.repository.activityRepository;

public class getUserActivitiesUseCase {

    public static Result<QueryResult<List<ActivityModel>,DocumentSnapshot>, Exception> getActivitiesByAdmin(String uuid, Integer count) throws InterruptedException{

        Result<QueryResult<List<ActivityModel>,DocumentSnapshot>, Exception> res = new Result<>();
        final activityRepository repository = new activityRepository();
        Thread t = new Thread(() -> {
            try {
                res.data = repository.getActivitiesByAdminId(uuid,count);

            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                res.exception = e;
            }
        });
        t.start();
        t.join();
        t.interrupt();
        return res;

    }
}
