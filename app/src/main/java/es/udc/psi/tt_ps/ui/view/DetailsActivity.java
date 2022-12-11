package es.udc.psi.tt_ps.ui.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;

import static es.udc.psi.tt_ps.domain.activity.joinAnActivity.joinAnActivity;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import es.udc.psi.tt_ps.R;
import es.udc.psi.tt_ps.data.model.ActivityModel;
import es.udc.psi.tt_ps.data.model.QueryResult;
import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.databinding.ActivityDetailsBinding;
import es.udc.psi.tt_ps.ui.viewmodel.ListActivities;


public class DetailsActivity extends AppCompatActivity implements OnMapReadyCallback {
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


        try {
            init(activitiesList);
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        setbuttons();
        updateButtonsState(activitiesList);
    }

    public void init(ListActivities activitiesList) throws ParseException, IOException {
        if (activitiesList.getActivityImage()!=null)
        {
            Glide.with(binding.cardMedia.getContext()).load(activitiesList.getActivityImage()).into(binding.cardMedia);
        }else
        {
            Glide.with(binding.cardMedia.getContext()).load(R.drawable.ic_launcher_background).into(binding.cardMedia);
        }

        binding.cardTitle.setText(activitiesList.getTitle());
        String StartDate = activitiesList.getStart_date().toString();
        String EndDate = activitiesList.getEnd_date().toString();
        //convert a String that represent a date in yyyy-MM-dd HH:mm:ss z format to String with format dd/MM/yyyy HH:mm
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        Date date = sdf.parse(StartDate);
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH);
        String StartDate2 = sdf2.format(date);
        Date date2 = sdf.parse(EndDate);
        String EndDate2 = sdf2.format(date2);
        binding.cardDate.setText(StartDate2 + " - " + EndDate2);
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(activitiesList.getLocation().getLatitude(), activitiesList.getLocation().getLongitude(), 1);
        String cityName = addresses.get(0).getLocality();
        binding.location.setText(cityName);
        binding.cardDescription.setText(activitiesList.getDescription());
        binding.cardParticipants.setText(activitiesList.getParticioants());

        showMap();
        binding.cardLocation.setOnClickListener(view1 -> {
            Uri navigationIntentUri = Uri.parse("http://maps.google.com/maps?q=loc:" + activitiesList.getLocation().getLatitude() + "," + activitiesList.getLocation().getLongitude() + " (" +binding.cardTitle.getText().toString()+ ")");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, navigationIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);

        });

    }

    private void showMap(){
        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.activity_map);


        supportMapFragment.getMapAsync(this);

    }

    public void setbuttons() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String currentUserId= user.getUid();

        binding.signup2.setOnClickListener(view1 -> {

            if (!activitiesList.getParticipants().contains(currentUserId)){
                try {
                    addUserToActivity(currentUserId);
                } catch (InterruptedException | ParseException | IOException e) {
                    e.printStackTrace();
                }

            }else{
                Toast.makeText(this, "Ya se ha unido a esta actividad", Toast.LENGTH_SHORT).show();
            }


        });

        binding.deleteButton.setOnClickListener(view1 -> {
            Toast.makeText(this, "implementar borrado", Toast.LENGTH_SHORT).show();

        });

        binding.updateButton.setOnClickListener(view1 -> {
            Toast.makeText(this, "implementar actualizar", Toast.LENGTH_SHORT).show();

        });

        binding.singup3.setOnClickListener(view1 -> {
            Toast.makeText(this, "implementar mostrar mapas", Toast.LENGTH_SHORT).show();

        });

        binding.closeButton.setOnClickListener(view1 -> {
            finish();
        });


    }

    public void addUserToActivity(String currentUserId) throws InterruptedException, ParseException, IOException {

        Result<QueryResult<ActivityModel,DocumentSnapshot>, Exception> res;
        if (!activitiesList.getParticipants().contains(currentUserId)){
            res=joinAnActivity(activitiesList.getActivityId());

            if(res.exception==null){
                ListActivities listActivities;
                listActivities=new ListActivities(res.data.cursor.getId(),res.data.data.getImage(),res.data.data.getTitle(),res.data.data.getLocation(),res.data.data.getEnd_date(),
                        res.data.data.getDescription(),res.data.data.getStart_date(),res.data.data.getCreation_date(),res.data.data.getAdminId(),res.data.data.getParticipants(),
                        res.data.data.getTags());
                init(listActivities);
                updateButtonsState(listActivities);
            }
        }else{
            Toast.makeText(this, "Ya se ha unido a esta actividad", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateButtonsState(ListActivities activitiesList){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String currentUserId= user.getUid();
        if(activitiesList.getAdminId().equals(currentUserId)){
            if (activitiesList.getParticipants().contains(currentUserId)){
                binding.signup2.setVisibility(View.INVISIBLE);
                binding.singup3.setVisibility(View.VISIBLE);
            }else {
                binding.singup3.setVisibility(View.INVISIBLE);
                binding.signup2.setVisibility(View.VISIBLE);
            }
        }else {
            binding.deleteButton.setVisibility(View.INVISIBLE);
            binding.updateButton.setVisibility(View.INVISIBLE);
            if (activitiesList.getParticipants().contains(currentUserId)){
                binding.signup2.setVisibility(View.INVISIBLE);
                binding.singup3.setVisibility(View.VISIBLE);
            }else {
                binding.singup3.setVisibility(View.INVISIBLE);
                binding.signup2.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        googleMap.clear();
        LatLng coord = new LatLng(activitiesList.getLocation().getLatitude(), activitiesList.getLocation().getLongitude());
        googleMap.addMarker(new MarkerOptions().position(coord).title("Marcador"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coord, 14));
    }
}
