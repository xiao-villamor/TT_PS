package es.udc.psi.tt_ps.domain.activity;

import java.io.FileNotFoundException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.data.repository.activityRepository;
import es.udc.psi.tt_ps.data.repository.userRepository;

public class uploadUserPicUseCase {

    public static Result<String, Exception> uploadUserPic(String uuid, byte[] image) throws FileNotFoundException, ExecutionException, InterruptedException, TimeoutException {

        Result<String, Exception> res = new Result<>();
        final userRepository repository = new userRepository();
        Thread t = new Thread(() -> {
            try {
                res.data = repository.uploadProfilePic(uuid, image);

            } catch (InterruptedException | ExecutionException | TimeoutException | FileNotFoundException e) {
                res.exception = e;
            }
        });
        t.start();
        t.join();
        t.interrupt();
        return res;

    }

}
