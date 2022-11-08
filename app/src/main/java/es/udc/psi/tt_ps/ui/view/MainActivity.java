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
import es.udc.psi.tt_ps.data.network.user.OnAuthStateChangeListener;
import es.udc.psi.tt_ps.data.repository.authRepository;
import es.udc.psi.tt_ps.databinding.ActivityMainBinding;
import es.udc.psi.tt_ps.ui.viewmodel.MainViewModel;
import es.udc.psi.tt_ps.ui.view.UserInfoActivity;


public class MainActivity extends AppCompatActivity implements OnAuthStateChangeListener {
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseStorage storage;
    private ActivityMainBinding binding;
    authRepository authRepository = new authRepository();
    MainViewModel mainViewModel = new MainViewModel(authRepository);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mainViewModel.setAuthStateChangeListener(this);

        firebaseConnection connection = new firebaseConnection();
        connection.connect(this);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.MANAGE_EXTERNAL_STORAGE}, 1);


        //Boton para inciar sesion con otra cuenta
        binding.login.setOnClickListener(v -> {
            mainViewModel.signOut();

        });

        binding.show.setOnClickListener(v->{
            Intent intentSend = new Intent(MainActivity.this, ActivityListActivities.class);
            startActivity(intentSend);
        });

        binding.userInfo.setOnClickListener(v->{
            Intent intentSend = new Intent(MainActivity.this, UserInfoActivity.class);
            startActivity(intentSend);
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        mainViewModel.addAuthListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mainViewModel.removeAuthListener();
    }

    @Override
    public void onAuthStateChanged(boolean isUserLoggedOut) {
        Intent userProfileIntent = new Intent(this, LogInActivity.class);
        startActivity(userProfileIntent);

    }

}