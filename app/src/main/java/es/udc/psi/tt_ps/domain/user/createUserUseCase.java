package es.udc.psi.tt_ps.domain.user;


import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.util.Date;
import java.util.List;

import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.data.model.UserModel;
import es.udc.psi.tt_ps.data.repository.userRepository;

public class createUserUseCase {

    public static Result<FirebaseUser, Exception> createUser (String name, String email, String password,
                              String surname, Date birthDate, String phone,
                              Uri pic, List<String> rsss, List<String> interests,List<Float> rating) throws InterruptedException {

        final userRepository repository = new userRepository();
        Result<FirebaseUser, Exception> res = new Result<>();

        UserModel user = new UserModel(name,surname,birthDate,email,phone,null,rsss,rating,interests);
        Thread thread = new Thread(() -> {
            try{
                repository.createUser(email,password,user,pic);
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
