package es.udc.psi.tt_ps.domain.activity;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

import es.udc.psi.tt_ps.data.repository.activityRepository;

public class ListenToActivityChangesUseCase {


    public static void listenToActivityChanges(String uuid, Consumer<String> function) {
        final activityRepository repository = new activityRepository();
        repository.ActivityListener(uuid, function);
    }
}
