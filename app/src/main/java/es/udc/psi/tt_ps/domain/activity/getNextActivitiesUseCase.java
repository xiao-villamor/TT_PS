package es.udc.psi.tt_ps.domain.activity;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import es.udc.psi.tt_ps.data.model.ActivityModel;
import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.data.repository.activityRepository;

public class getNextActivitiesUseCase {

    public static Result<List<ActivityModel>, Exception> getActivities() throws InterruptedException{

        Result<List<ActivityModel>, Exception> res = new Result<>();
        final activityRepository repository = new activityRepository();
        Thread t = new Thread(() -> {
            try {
                res.data = repository.getNextActivities();

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
