package es.udc.psi.tt_ps;

import org.junit.Test;

import static org.junit.Assert.*;

import androidx.annotation.NonNull;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import java.sql.Date;

import es.udc.psi.tt_ps.data.model.UserModel;
import es.udc.psi.tt_ps.data.userRepository;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void createUser() {

        userRepository r;
        UserModel u;
        r = new userRepository();

        u = new UserModel("12345", "Pepe", "Perez", Date.valueOf("1999-01-01"), "dev@mail.com",
                "666666666", "profilePic", null, null, null);
        r.createUser(u);
    }
}