package es.udc.psi.tt_ps.ui.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import es.udc.psi.tt_ps.R;
import es.udc.psi.tt_ps.data.model.UserModel;
import es.udc.psi.tt_ps.data.userRepository;
import es.udc.psi.tt_ps.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        userRepository r;
        r = new userRepository(mAuth, db);
        UserModel u;
        List<String> tag = new ArrayList<String>();
        tag.add("tag1");
        List<String> rss = new ArrayList<String>();
        rss.add("das");
        List<Float> ca = new ArrayList<Float>();
        ca.add(2.0f);


        u = new UserModel("pakirrin1234", "Paco", "Perez", Date.valueOf("1999-01-01"), "2313dasda@gmail.com",
                "666666666", "profilePic", rss, ca, tag);

        r.createUser(u);


        binding.button.setOnClickListener(v -> {
            //execute a async function and when it finishes, do something with the value returned
            CompletableFuture<UserModel> userModel = CompletableFuture.supplyAsync(() -> r.getUser(mAuth.getUid()));
            userModel.thenAccept(user -> {
                Log.d("TAG", user.getEmail());
                updateText(user.getEmail());
            });
       });




    }

    public void updateText(String text){
        binding.textView.setText(text);
    }

}