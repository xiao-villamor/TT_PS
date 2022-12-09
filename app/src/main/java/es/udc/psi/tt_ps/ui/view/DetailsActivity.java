package es.udc.psi.tt_ps.ui.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.auth.User;


import es.udc.psi.tt_ps.R;
import es.udc.psi.tt_ps.databinding.ActivityDetailsBinding;
import es.udc.psi.tt_ps.ui.viewmodel.ListActivities;


public class DetailsActivity extends AppCompatActivity {
    private ActivityDetailsBinding binding;
    ListActivities activitiesList;
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        view = binding.getRoot();
        setContentView(view);
        Log.d("Tag","details");
        Bundle extras = getIntent().getExtras();
        activitiesList= (ListActivities) extras.get("events");
        GeoPoint coord = new GeoPoint(extras.getDouble("latitud"),extras.getDouble("longitud"));
        activitiesList.setLocation(coord);



        init();
        setbuttons();
    }

    public void init(){
        if (activitiesList.getActivityImage()!=null)
        {
            Glide.with(binding.cardMedia.getContext()).load(activitiesList.getActivityImage()).into(binding.cardMedia);
        }else
        {
            Glide.with(binding.cardMedia.getContext()).load(R.drawable.ic_launcher_background).into(binding.cardMedia);
        }

        binding.cardTitle.setText(activitiesList.getTitle());
        binding.cardStartDate.setText(activitiesList.getStart_date().toString());
        binding.cardEndDate.setText(activitiesList.getEnd_date().toString());
        binding.cardCreationDate.setText(activitiesList.getCreation_date().toString());
        binding.cardLocation.setText(activitiesList.getLocation().toString());
        binding.cardDescription.setText(activitiesList.getDescription());
        binding.cardParticipants.setText(activitiesList.getParticioants());

    }

    public void setbuttons(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        binding.signup2.setOnClickListener(view1 -> {
            Toast.makeText(this, "implementar unirse", Toast.LENGTH_SHORT).show();

        });

        binding.deleteButton.setOnClickListener(view1 -> {
            Toast.makeText(this, "implementar borrado", Toast.LENGTH_SHORT).show();

        });

        binding.updateButton.setOnClickListener(view1 -> {
            Toast.makeText(this, "implementar actualizar", Toast.LENGTH_SHORT).show();

        });

        binding.floatingActionButtonMaps.setOnClickListener(view1 -> {
            Toast.makeText(this, "implementar mostrar mapas", Toast.LENGTH_SHORT).show();

        });


        assert user != null;
        if(!activitiesList.getAdminId().equals(user.getUid())){
            binding.signup2.setVisibility(View.VISIBLE);
            binding.deleteButton.setVisibility(View.INVISIBLE);
            binding.updateButton.setVisibility(View.INVISIBLE);
        }else{
            binding.signup2.setVisibility(View.VISIBLE);//poner a invisible, ahora esta visible porque todas las
            binding.deleteButton.setVisibility(View.VISIBLE);//actividades fueron creadas por este user
            binding.updateButton.setVisibility(View.VISIBLE);
        }

    }

}