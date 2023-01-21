package es.udc.psi.tt_ps.domain.activity;

import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.data.repository.userRepository;


public class updatePasswordUseCase {

    public static Result<Object, Exception> updateUserPassword(String password) throws InterruptedException {
        final userRepository repository = new userRepository();
        Result<Object, Exception> res = new Result<>();

        Thread thread1 = new Thread(() -> {
            try {
                repository.updateUserPassword(password);
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
