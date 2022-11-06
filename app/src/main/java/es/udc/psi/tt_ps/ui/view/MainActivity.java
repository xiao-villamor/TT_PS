package es.udc.psi.tt_ps.ui.view;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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

import es.udc.psi.tt_ps.core.firebaseConnection;
import es.udc.psi.tt_ps.data.model.ActivityModel;
import es.udc.psi.tt_ps.data.model.UserModel;
import es.udc.psi.tt_ps.data.repository.activityRepository;
import es.udc.psi.tt_ps.data.repository.userRepository;
import es.udc.psi.tt_ps.databinding.ActivityMainBinding;
import es.udc.psi.tt_ps.domain.user.createUserUseCase;

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
        firebaseConnection connection = new firebaseConnection();
        connection.connect(this);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.MANAGE_EXTERNAL_STORAGE}, 1);

        userRepository r;
        activityRepository ar;

        r = new userRepository();
        ar = new activityRepository();

        UserModel u;
        ActivityModel a;

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        File path  = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path,"04em0x0gb1t61.jpg");

        u = new UserModel("name","surname",Date.valueOf("2021-01-01"),"dev@mail.com","66666666","",null,null,null);
        a = new ActivityModel("amusement park","Going to an amusement park", timestamp, timestamp,timestamp,null,"as",null,null);


        createUserUseCase c = new createUserUseCase();
        try {
            c.createUser("name","dev@mail.com","123456","sur",
                    Date.valueOf("2021-01-01"),"",file,null,null);
        } catch (InterruptedException e) {
            Log.d("User already exists",e.getMessage());
        }


        //Se comprueba si existe un usuario loggeado
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null){
            Log.d("TAG", "Usuario: "+ mAuth.getCurrentUser().getEmail());
        }else{
            Log.d("TAG", "No existe usuario registrado");
            Intent userProfileIntent = new Intent(this, LogInActivity.class);
            startActivity(userProfileIntent);
        }

        //Boton para inciar sesion con otra cuenta
        binding.login.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent userProfileIntent = new Intent(this, LogInActivity.class);
            startActivity(userProfileIntent);
        });

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

        binding.show.setOnClickListener(v->{
            Intent intentSend = new Intent(MainActivity.this, ActivityListActivities.class);
            startActivity(intentSend);
        });


    }

}