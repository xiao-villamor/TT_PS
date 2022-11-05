package es.udc.psi.tt_ps.domain.user;

import java.io.File;
import java.util.Date;
import java.util.List;

import es.udc.psi.tt_ps.data.model.UserModel;
import es.udc.psi.tt_ps.data.repository.userRepository;

public class createUserUseCase {

    private final userRepository repository = new userRepository();

    public void createUser (String name, String email, String password,
                            String surname, Date birthDate, String phone,
                            File pic, List<String> rsss,List<String> interests) throws InterruptedException {

        UserModel user = new UserModel(name,surname,birthDate,email,phone,null,rsss,null,interests);

        Thread thread = new Thread(() -> repository.createUser(email,password,user,pic));
        thread.start();
        thread.join();
    }

}
