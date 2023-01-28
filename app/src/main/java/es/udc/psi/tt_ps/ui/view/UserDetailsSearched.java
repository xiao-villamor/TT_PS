package es.udc.psi.tt_ps.ui.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import es.udc.psi.tt_ps.databinding.ActivitySearchuserDetailsBinding;
import es.udc.psi.tt_ps.ui.adapter.tagAdapter;
import es.udc.psi.tt_ps.ui.viewmodel.ListUsers;

public class UserDetailsSearched extends AppCompatActivity {
    private ActivitySearchuserDetailsBinding binding;
    ListUsers listUsers;
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchuserDetailsBinding.inflate(getLayoutInflater());
        view = binding.getRoot();
        setContentView(view);
        Bundle extras = getIntent().getExtras();
        listUsers= (ListUsers) extras.get("user");
        
        init();
        
        
    }

    void init(){

        try {
            Glide.with(this).load(listUsers.getProfilePic()).into(binding.profilePic2);
        }catch (Exception e){
            Log.d("_TAG","no profile pic");
        }

        binding.Name2.setText(listUsers.getName());
        Log.d("_TAG",listUsers.getRating().toString());
        binding.rating2.setRating(listUsers.getRating());
        binding.ratingCnt2.setText("("+listUsers.getRating().toString()+")");
        tagAdapter tagAdapter = new tagAdapter(listUsers.getInterests().toArray(new String[0]));
        binding.simpleGridView2.setAdapter(tagAdapter);
        if(listUsers.getDescription() != null){
            binding.desc2.setText(listUsers.getDescription());
        }
        
    }
    
}
