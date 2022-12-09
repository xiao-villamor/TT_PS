package es.udc.psi.tt_ps.domain.activity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import es.udc.psi.tt_ps.data.model.ActivityModel;
import es.udc.psi.tt_ps.data.model.QueryResult;
import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.data.repository.activityRepository;
import es.udc.psi.tt_ps.ui.viewmodel.ListActivities;

public class joinAnActivity {
    public static Result<List<ActivityModel>, Exception> joinAnActivity(ListActivities listActivities) throws InterruptedException {

        final activityRepository repository = new activityRepository();
        Result<List<ActivityModel>, Exception> res = new Result<>();
        AtomicReference<QueryResult<List<ActivityModel>,List<DocumentSnapshot>>> result = new AtomicReference<>(new QueryResult<>());
        String documentid=null;
        
        ActivityModel activity = new ActivityModel();
        
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String currentUserId= user.getUid();
        
        Thread thread1 = new Thread(() -> {
            try {
                result.set(repository.getActivitiesByAdmin(listActivities.getActivityId()));
            } catch (Exception e) {
                res.exception = e;
            }
        });
        thread1.start();
        thread1.join();
        thread1.interrupt();



        return res;
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
        }

        }




