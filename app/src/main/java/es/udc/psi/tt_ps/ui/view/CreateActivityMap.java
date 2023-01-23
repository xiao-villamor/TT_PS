package es.udc.psi.tt_ps.ui.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import es.udc.psi.tt_ps.R;
import es.udc.psi.tt_ps.ui.view.MapFragment;

public class CreateActivityMap extends FragmentActivity implements MapFragment.OnPointSelected, OnMapReadyCallback {

    double latitude;
    double longitude;
    boolean pointed;
    Button boton;
    SearchView searchView;
    GoogleMap mMap;
    String LAT_KEY = "latitud_mapa";
    String LON_KEY = "longitud_mapa";
    SupportMapFragment mapFragment;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_map);


        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        boton=findViewById(R.id.pointButton);
        searchView=findViewById(R.id.searchView);
        pointed=false;



        Fragment fragment = new MapFragment();


        /*
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .commit();

         */

        boton.setOnClickListener(v -> {
            if(!pointed){
                Toast.makeText(getApplicationContext(), "You must select a coordinates to submit", Toast.LENGTH_SHORT).show();
            }else{
                //enviar los resultados
                Intent intent = new Intent();
                intent.putExtra(LAT_KEY, latitude);
                intent.putExtra(LON_KEY, longitude);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;

                if (location != null || !location.equals("")) {
                    Geocoder geocoder = new Geocoder(CreateActivityMap.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if(addressList.size()==0){
                        Toast.makeText(getApplicationContext(), "No results found", Toast.LENGTH_SHORT).show();
                    }else {
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                        latitude = latLng.latitude;
                        longitude = latLng.longitude;
                        pointed = true;
                    }

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        mapFragment.getMapAsync(this);


    }


    public void onPointed(double lat, double longt){
        pointed=true;
        latitude=lat;
        longitude=longt;
        Log.d("TAG", "Selecionadas las coordendas: " + lat + " : " + longt);
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
                latitude=latLng.latitude;
                longitude=latLng.longitude;
                pointed=true;
            }
        });

    }
}