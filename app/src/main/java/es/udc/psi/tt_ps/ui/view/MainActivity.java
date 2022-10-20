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

import es.udc.psi.tt_ps.R;
import es.udc.psi.tt_ps.data.model.UserModel;
import es.udc.psi.tt_ps.data.userRepository;
import es.udc.psi.tt_ps.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        es.udc.psi.tt_ps.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        FirebaseApp.initializeApp(this);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();


        userRepository r;
        r = new userRepository(mAuth,db);
        UserModel u;

        u = new UserModel("pakirrin1234", "Pepe", "Perez", Date.valueOf("1999-01-01"), "2313dasda@gmail.com",
                "666666666", "profilePic", null, null, null);

        r.createUser(u);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        binding.textView.setText(db.collection("User_Info").document(currentUser.getUid()).toString());
        UserModel c = new UserModel("pakirrin1234", "edit", "Perez", Date.valueOf("1999-01-01"), "mailRand@gmail.com",
        "1312", "profilePic", null, null, null);
        r.updateUser(c);

    }
}