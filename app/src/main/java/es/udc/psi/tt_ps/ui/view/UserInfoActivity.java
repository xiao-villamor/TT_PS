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
import es.udc.psi.tt_ps.ui.adapter.tagAdapter;

public class UserInfoActivity extends AppCompatActivity {

    private ActivityUserInfoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserInfoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        UserModel u = null;
        Log.d("_TAG",firebaseConnection.getUser().toString());
        try {
             u = getUserInfoUseCase.getInfo(firebaseConnection.getUser()).data;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.d("_TAG","set user info");

        if(u.getProfilePic() != null){
            Glide.with(this)
                    .load(u.getProfilePic())
                    .into(binding.profilePic);
        }

        binding.Name.setText(u.getName());
        binding.rating.setRating(u.getRating().get(0));
        tagAdapter tagAdapter = new tagAdapter(u.getInterests().toArray(new String[0]));
        binding.simpleGridView.setAdapter(tagAdapter);
        binding.desc.setText(u.getDescription());
    }
}
