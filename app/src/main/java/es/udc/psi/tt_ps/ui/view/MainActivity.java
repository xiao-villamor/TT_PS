package es.udc.psi.tt_ps.ui.view;

import static java.lang.Thread.sleep;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import es.udc.psi.tt_ps.data.model.UserModel;
import es.udc.psi.tt_ps.data.userRepository;
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
        r = new userRepository(mAuth, db, storage);
        UserModel u;
        List<String> tag = new ArrayList<String>();
        tag.add("tag1");
        List<String> rss = new ArrayList<String>();
        rss.add("das");
        List<Float> ca = new ArrayList<Float>();
        ca.add(2.0f);


        Thread t = new Thread(){
            @Override
            public void run(){
                r.loginUser("dev3@devmail.com","123456");
            }
        };
        t.start();

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        File path  = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS); ;
        File file = new File(path,"04em0x0gb1t61.jpg");

        binding.button.setOnClickListener(v -> {
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


        }





    public void updateText(String text){
        binding.textView.setText(text);
    }

}