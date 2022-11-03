package es.udc.psi.tt_ps.ui.view;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import es.udc.psi.tt_ps.data.model.ActivityModel;
import es.udc.psi.tt_ps.data.model.UserModel;
import es.udc.psi.tt_ps.data.repository.activityRepository;
import es.udc.psi.tt_ps.data.repository.userRepository;
import es.udc.psi.tt_ps.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseStorage storage;
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
        storage = FirebaseStorage.getInstance();
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.MANAGE_EXTERNAL_STORAGE}, 1);

        userRepository r;
        activityRepository ar;

        r = new userRepository(mAuth, db, storage);
        ar = new activityRepository(mAuth, db, storage);

        UserModel u;
        ActivityModel a;

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        File path  = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path,"04em0x0gb1t61.jpg");

        u = new UserModel("name","surname",Date.valueOf("2021-01-01"),"dev@mail.com","66666666","",null,null,null);
        a = new ActivityModel("amusement park","Going to an amusement park", timestamp, timestamp,timestamp,null,mAuth.getUid(),null,null);
        Thread t = new Thread(){
            @Override
            public void run() {
                r.loginUser("dev3@mail.com", "123456");
                //for 10 times
            }
        };
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



        binding.button.setOnClickListener(v -> {
            AtomicReference<List<ActivityModel>> data = new AtomicReference<>();

            Thread thread = new Thread(){
                @Override
                public void run(){
                    try {
                        data.set(ar.getActivities());
                    } catch (TimeoutException | ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            AtomicReference<String> res = new AtomicReference<>();
            data.get().forEach(activityModel -> res.set(res.get()+activityModel.getDescription()+"\n"));

            binding.textView.setText(res.get());



            /*
            AtomicReference<String> uri = new AtomicReference<>();

            Log.d("TAG", "button " + mAuth.getCurrentUser().getUid());

            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        uri.set(r.uploadProfilePic(mAuth.getUid(),file));
                    } catch (ExecutionException | TimeoutException | InterruptedException | FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //Carga de imagen con glide ejemplo para obtencion profilePic
            Glide.with(this)
                    .load(uri.get())
                    .into(binding.imageView);

            /*
            AtomicReference<UserModel> user = new AtomicReference<>();

            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        user.set(r.getUser("7bAvGnfNm3eVyNedXHfvh2us1Dd2"));
                    } catch (ExecutionException | TimeoutException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            updateText(user.get().getEmail());
             */

        });
        binding.next.setOnClickListener(v -> {
            AtomicReference<List<ActivityModel>> data = new AtomicReference<>();

            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        data.set(ar.getNextActivities());
                    } catch (TimeoutException | ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            AtomicReference<String> res = new AtomicReference<>();
            data.get().forEach(activityModel -> res.set(res.get() + activityModel.getDescription() + "\n"));

            binding.textView.setText(res.get());
        });


        }


}