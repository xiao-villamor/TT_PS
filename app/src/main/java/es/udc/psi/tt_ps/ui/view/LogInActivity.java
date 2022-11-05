package es.udc.psi.tt_ps.ui.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.data.repository.userRepository;
import es.udc.psi.tt_ps.databinding.ActivityLogInBinding;
import es.udc.psi.tt_ps.domain.user.loginUserUseCase;

public class LogInActivity extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseStorage storage;
    private ActivityLogInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLogInBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        userRepository r;

        r = new userRepository();
        mAuth = FirebaseAuth.getInstance();

        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginUserUseCase c = new loginUserUseCase();
                try {
                    Log.d("TAG", "Logeando con: "+ binding.email.getText().toString() + " - " + binding.password.getText().toString()+" ...");
                    Result res=c.loginUser(binding.email.getText().toString(), binding.password.getText().toString());
                    //c.loginUser("fg@ffver.com", "sdf");
                    //Result res= c.loginUser("dev_m@mail.com", "123456");
                    //Log.d("TAG", res.data.toString());
                    Log.d("TAG", res.exception.getMessage());

                    //Log.d("TAG","se inicio sesion correctamente ");
                } catch (InterruptedException e) {
                    Log.d("User does not exists",e.getMessage());
                    Log.d("TAG", "Imposible inciar sesion");
                }





                if(mAuth.getCurrentUser()!=null){
                    Log.d("TAG","se inicio sesion correctamente ");
                }else{
                    Log.d("TAG","No se inicio sesion ");
                }



            }


        });

    }
}