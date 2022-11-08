package es.udc.psi.tt_ps.ui.view;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;


import es.udc.psi.tt_ps.core.firebaseConnection;
import es.udc.psi.tt_ps.data.model.UserModel;
import es.udc.psi.tt_ps.databinding.ActivityUserInfoBinding;
import es.udc.psi.tt_ps.domain.user.getUserInfoUseCase;

public class UserInfoActivity extends AppCompatActivity {

    private ActivityUserInfoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserInfoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        UserModel u = null;
        try {
             u = getUserInfoUseCase.getInfo(firebaseConnection.getUser()).data;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(u.getProfilePic() != null){
            Glide.with(this)
                    .load(u.getProfilePic())
                    .into(binding.imageView1);
        }

        binding.userName.setText(u.getName());
        binding.userSurname.setText(u.getSurname());
        binding.userEmail.setText(u.getEmail());
        binding.userPhone.setText(u.getPhone());
        binding.userBirthdate.setText(u.getBirthDate().toString());
    }
}
