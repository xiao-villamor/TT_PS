package es.udc.psi.tt_ps.ui.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.GeoPoint;

import java.util.List;

import es.udc.psi.tt_ps.R;
import es.udc.psi.tt_ps.databinding.ActivityDetailsBinding;
import es.udc.psi.tt_ps.ui.viewmodel.ListActivities;


public class DetailsActivity extends AppCompatActivity {
    private ActivityDetailsBinding binding;
    ListActivities activitiesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        Log.d("Tag","details");
        Bundle extras = getIntent().getExtras();
        activitiesList= (ListActivities) extras.get("events");
        GeoPoint coord = new GeoPoint(extras.getDouble("latitud"),extras.getDouble("longitud"));
        activitiesList.setLocation(coord);

        init();
    }

    public void init(){
        if (activitiesList.getActivityImage()!=null)
        {
            Glide.with(binding.cardMedia.getContext()).load(activitiesList.getActivityImage()).into(binding.cardMedia);
        }else
        {
            Glide.with(binding.cardMedia.getContext()).load(R.drawable.ic_launcher_background).into(binding.cardMedia);
        }

        binding.cardTitle.setText(activitiesList.title);
        binding.cardStartDate.setText(activitiesList.start_date.toString());
        binding.cardEndDate.setText(activitiesList.end_date.toString());
        binding.cardCreationDate.setText(activitiesList.creation_date.toString());
        binding.cardLocation.setText(activitiesList.getLocation().toString());
        binding.cardDescription.setText(activitiesList.getDescription());
        binding.cardParticipants.setText(activitiesList.getParticioants());




    }

}