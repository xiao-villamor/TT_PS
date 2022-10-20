package es.udc.psi.tt_ps;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import java.sql.Date;

import es.udc.psi.tt_ps.data.model.UserModel;
import es.udc.psi.tt_ps.data.userRepository;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        userRepository r;
        UserModel u;
        r = new userRepository();

        u = new UserModel("12345", "Pepe", "Perez", Date.valueOf("1999-01-01"), "dev@mail.com",
                "666666666", "profilePic", null, null, null);
        r.createUser(u);
    }
}