package es.udc.psi.tt_ps.domain.user;

import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.data.model.UserModel;
import es.udc.psi.tt_ps.data.repository.userRepository;

public class editUserInfoUseCase {

    public static Result<Object, Exception> updateEditedUser(UserModel user) throws InterruptedException {

        final userRepository repository = new userRepository();
        Result<Object, Exception> res = new Result<>();

        Thread thread1 = new Thread(() -> {
            try {
                repository.updateUser(user);
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
