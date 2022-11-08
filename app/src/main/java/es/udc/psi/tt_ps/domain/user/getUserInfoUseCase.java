package es.udc.psi.tt_ps.domain.user;

import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.util.Date;
import java.util.List;

import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.data.model.UserModel;
import es.udc.psi.tt_ps.data.repository.userRepository;

public class getUserInfoUseCase {
    public static Result<UserModel, Exception> getInfo (String uuid) throws InterruptedException {
        final userRepository repository = new userRepository();
        Result<UserModel, Exception> res = new Result<>();

        Thread thread = new Thread(() -> {
            try{
                res.data = repository.getUser(uuid);
            }catch (Exception e){
                res.exception = e;
            }
        });
        thread.start();
        thread.join();
        thread.interrupt();

        return res;
    }
}
