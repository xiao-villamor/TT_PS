package es.udc.psi.tt_ps.domain.user;

import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.data.repository.userRepository;

public class loginUserUseCase {


    public static Result loginUser(String username, String password) throws InterruptedException {
        Result<FirebaseUser, Exception> res = new Result<>();
        final userRepository repository = new userRepository();
        Thread t = new Thread(() -> {
            try {
                repository.loginUser(username,password);
                ;
            } catch (ExecutionException | InterruptedException | TimeoutException e) {
                res.exception = e;
            }
        });
        t.start();
        t.join();

        return res;

    }


}
