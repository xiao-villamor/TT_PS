package es.udc.psi.tt_ps.ui.view;

import android.content.Context;
import android.graphics.Camera;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import es.udc.psi.tt_ps.R;


public class MapFragment extends Fragment {

    OnPointSelected clickedListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);



        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                //Cuando el mapa este cargado
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng latLng) {
                        //Cuando se haga click en el mapa
                        MarkerOptions markerOptions = new MarkerOptions();

                        markerOptions.position(latLng);
                        markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                        googleMap.clear();
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                latLng, 10
                        ));

                        googleMap.addMarker(markerOptions);
                        clickedListener.onPointed(latLng.latitude, latLng.longitude);
                    }
                });
            }
        });

        return view;

    }


    public interface OnPointSelected {
        public void onPointed(double lat, double longt);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            clickedListener = (OnPointSelected) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " implement OnArticleSelectedListener");
        }
    }

}