package es.udc.psi.tt_ps.ui.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import java.sql.Date;

import es.udc.psi.tt_ps.R;
import es.udc.psi.tt_ps.data.model.UserModel;
import es.udc.psi.tt_ps.data.userRepository;
import es.udc.psi.tt_ps.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        es.udc.psi.tt_ps.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        FirebaseApp.initializeApp(this);

        userRepository r;
        r = new userRepository();
        UserModel u;

        u = new UserModel("pakirrin1234", "Pepe", "Perez", Date.valueOf("1999-01-01"), "dev@gmail.com",
                "666666666", "profilePic", null, null, null);

        r.createUser(u);
        //binding.textView.setText(r.createUser(u));

    }
}