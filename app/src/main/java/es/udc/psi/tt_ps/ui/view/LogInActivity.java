package es.udc.psi.tt_ps.ui.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;

import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.databinding.ActivityLoginBinding;
import es.udc.psi.tt_ps.domain.user.loginUserUseCase;

public class LogInActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        binding.button2.setOnClickListener(v -> {

            try {
                Result<FirebaseUser, Exception> res = loginUserUseCase.loginUser(binding.email.getText().toString(), binding.password.getText().toString());
                if(res.exception != null){
                    Log.d("TAG", res.exception.toString());
                }
                else{
                    Log.d("TAG", "login correcto");
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });

    }
}
