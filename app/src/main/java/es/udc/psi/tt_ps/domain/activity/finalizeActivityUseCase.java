package es.udc.psi.tt_ps.domain.activity;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import es.udc.psi.tt_ps.data.repository.activityRepository;

public class finalizeActivityUseCase {
    public static void finalizeActivityUseCase(String activityId) throws InterruptedException{

        final activityRepository repository = new activityRepository();

        Thread t = new Thread(() -> {
            try {
                repository.finalizeActivity(activityId);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        });

        t.start();
        t.join();
        t.interrupt();
    }
}
