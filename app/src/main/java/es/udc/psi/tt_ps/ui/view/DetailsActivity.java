package es.udc.psi.tt_ps.ui.view;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

import static es.udc.psi.tt_ps.domain.activity.joinAnActivityUseCase.joinAnActivity;
import static es.udc.psi.tt_ps.domain.activity.unsubscribeActivityUseCase.unsubscribeActivity;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.udc.psi.tt_ps.R;
import es.udc.psi.tt_ps.data.model.ActivityModel;
import es.udc.psi.tt_ps.data.model.QueryResult;
import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.databinding.ActivityDetailsBinding;
import es.udc.psi.tt_ps.domain.activity.deleteActivityUseCase;
import es.udc.psi.tt_ps.ui.fragments.ActivityListFragment;
import es.udc.psi.tt_ps.ui.viewmodel.ListActivities;


public class DetailsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private ActivityDetailsBinding binding;
    ListActivities activitiesList;
    View view;
    String currentUserId;
    FirebaseUser user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        view = binding.getRoot();
        setContentView(view);
        Bundle extras = getIntent().getExtras();
        activitiesList= (ListActivities) extras.get("events");

        GeoPoint coord = new GeoPoint(extras.getDouble("latitud"),extras.getDouble("longitud"));
        activitiesList.setLocation(coord);
        user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = user.getUid();

        Log.d("TAG", "ID: " + activitiesList.getActivityId());
        if(activitiesList.getParticipants().contains(currentUserId)){
            binding.signup2.setText("Unsubscribe");
        }



        try {
            init();
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        setbuttons();
    }


    public void init() throws ParseException, IOException {
        if (activitiesList.getActivityImage()!="")
        {
            Glide.with(binding.cardMedia.getContext()).load(activitiesList.getActivityImage()).into(binding.cardMedia);
        }else
        {
            Glide.with(binding.cardMedia.getContext()).load("https://firebasestorage.googleapis.com/v0/b/tt-ps-f0782.appspot.com/o/activity_image%2FJyu0z9Zdy106suj8hD5n.jpg?alt=media&token=6c4bf33d-4ce7-4ed2-8527-8b2c102d609f").into(binding.cardMedia);
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
        if(cityName == null){
            cityName = addresses.get(0).getSubAdminArea();
        }
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
                    (MainActivity.getInstance()).dataChanged(activitiesList.getActivityId(),activitiesList,"edit");
                } catch (InterruptedException | ParseException | IOException e) {
                    e.printStackTrace();
                }

            }else{
                try {
                    unsubscribeUser(currentUserId);
                    (MainActivity.getInstance()).dataChanged(activitiesList.getActivityId(),activitiesList,"edit");

                } catch (InterruptedException | ParseException | IOException e) {
                    e.printStackTrace();
                }
            }


        });

        binding.deleteButton.setOnClickListener(view1 -> {
            try {
                deleteActivityUseCase.deleteActivity(activitiesList.getActivityId());
                (MainActivity.getInstance()).dataChanged(activitiesList.getActivityId(),activitiesList,"del");
                finish();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });


        binding.updateButton.setOnClickListener(view1 -> {
            Intent intent = new Intent(this, EditActivity.class);
            intent.putExtra("activity", activitiesList);
            intent.putExtra("latitud",activitiesList.getLocation().getLatitude());
            intent.putExtra("longitud",activitiesList.getLocation().getLongitude());
            startEditActivity.launch(intent);


        });
        binding.closeButton.setOnClickListener(view1 -> {
            finish();
        });
    }

    public void addUserToActivity(String currentUserId) throws InterruptedException, ParseException, IOException {

        Result<QueryResult<ActivityModel,DocumentSnapshot>, Exception> res;
        if (!activitiesList.getParticipants().contains(currentUserId)){
            res=joinAnActivity(activitiesList.getActivityId(),currentUserId);

            if(res.exception==null){
                activitiesList.getParticipants().add(currentUserId);
                init();
                updateButtonsState("join");
            }
        }else{
            Toast.makeText(this, "Ya se ha unido a esta actividad", Toast.LENGTH_SHORT).show();
        }
    }
    public void unsubscribeUser(String currentUserId) throws InterruptedException,ParseException,IOException{
        Result<QueryResult<ActivityModel,DocumentSnapshot>, Exception> res;

        res=unsubscribeActivity(activitiesList.getActivityId(),currentUserId);
        if(res.exception==null){
            activitiesList.getParticipants().remove(currentUserId);
            init();
            updateButtonsState("unsubscribe");
        }


    }

    public void updateButtonsState(String state){
        //make switch case statement
        if(state.equals("join")){
            binding.signup2.setText("Unsubscribe");
        }else if(state.equals("unsubscribe")){
            binding.signup2.setText("Join");
        }


    }

    ActivityResultLauncher<Intent> startEditActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                    Intent data = result.getData();
                    Bundle bundle = data.getExtras();
                    if (bundle!= null) {
                        ListActivities newAct= (ListActivities) bundle.get("newActivity");
                        GeoPoint coord = new GeoPoint(bundle.getDouble("latitud"),bundle.getDouble("longitud"));
                        newAct.setLocation(coord);
                        activitiesList=newAct;
                        try {
                            init();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        googleMap.clear();
        LatLng coord = new LatLng(activitiesList.getLocation().getLatitude(), activitiesList.getLocation().getLongitude());
        googleMap.addMarker(new MarkerOptions().position(coord).title("Marcador"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coord, 14));
    }
}
