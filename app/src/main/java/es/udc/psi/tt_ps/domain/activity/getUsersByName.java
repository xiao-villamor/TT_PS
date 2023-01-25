package es.udc.psi.tt_ps.domain.activity;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import es.udc.psi.tt_ps.data.model.ActivityModel;
import es.udc.psi.tt_ps.data.model.QueryResult;
import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.data.model.UserModel;
import es.udc.psi.tt_ps.data.repository.activityRepository;
import es.udc.psi.tt_ps.data.repository.userRepository;

public class getUsersByName {
    public static Result<List<UserModel>,Exception> getUsersByName(String name) throws InterruptedException{

        Result<List<UserModel>, Exception> res = new Result<>();
        final userRepository repository = new userRepository();
        Thread t = new Thread(() -> {
            try {
                res.data = repository.getUserByUsername(name);

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
