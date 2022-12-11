package es.udc.psi.tt_ps.ui.view;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.auth.User;

import static es.udc.psi.tt_ps.domain.activity.joinAnActivity.joinAnActivity;
import es.udc.psi.tt_ps.R;
import es.udc.psi.tt_ps.data.model.ActivityModel;
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

    public void setbuttons(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String currentUserId= user.getUid();
        binding.signup2.setOnClickListener(view1 -> {
            try {
                Toast.makeText(this, activitiesList.getActivityId(), Toast.LENGTH_SHORT).show();
                joinAnActivity(activitiesList);
            } catch (InterruptedException e) {
                Log.d("TAG","error al unirse");
            }
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

        binding.updateButton.setOnClickListener(view1 -> {
            Intent intent = new Intent(this, EditActivity.class);
            intent.putExtra("activity", activitiesList);
            intent.putExtra("latitud",activitiesList.getLocation().getLatitude());
            intent.putExtra("longitud",activitiesList.getLocation().getLongitude());
            startEditActivity.launch(intent);

        });

        if(!activitiesList.getAdminId().equals(currentUserId)){
            binding.signup2.setVisibility(View.VISIBLE);
            binding.deleteButton.setVisibility(View.INVISIBLE);
            binding.updateButton.setVisibility(View.INVISIBLE);
        }else{
            binding.signup2.setVisibility(View.VISIBLE);//poner a invisible, ahora esta visible porque todas las
            binding.deleteButton.setVisibility(View.VISIBLE);//actividades fueron creadas por este user
            binding.updateButton.setVisibility(View.VISIBLE);
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
                        init();
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