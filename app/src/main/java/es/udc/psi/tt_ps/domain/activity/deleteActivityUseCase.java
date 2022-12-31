package es.udc.psi.tt_ps.domain.activity;

import es.udc.psi.tt_ps.data.repository.activityRepository;

public class deleteActivityUseCase {
    public static void deleteActivity(String activityId) throws InterruptedException{

        final activityRepository repository = new activityRepository();

        Thread t = new Thread(() -> {
            repository.deleteActivity(activityId);
        });

        t.start();
        t.join();
        t.interrupt();
    }
}

