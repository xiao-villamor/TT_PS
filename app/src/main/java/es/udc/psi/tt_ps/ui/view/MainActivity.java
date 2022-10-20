package es.udc.psi.tt_ps.ui.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Date;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.TimeUnit;

import es.udc.psi.tt_ps.R;
import es.udc.psi.tt_ps.data.model.UserModel;
import es.udc.psi.tt_ps.data.userRepository;
import es.udc.psi.tt_ps.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        es.udc.psi.tt_ps.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        userRepository r;
        r = new userRepository(mAuth,db);
        UserModel u;
        u = new UserModel("pakirrin1234", "Pepe", "Perez", Date.valueOf("1999-01-01"), "2313dasda@gmail.com",
                "666666666", "profilePic", null, null, null);

        r.createUser(u);

        binding.button.setOnClickListener(v -> {
            r.deleteUser();
        });

    }

}