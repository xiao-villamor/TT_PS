package es.udc.psi.tt_ps.ui.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

import es.udc.psi.tt_ps.R;

public class CreateActivityMap extends AppCompatActivity implements MapFragment.OnPointSelected {

    double latitude;
    double longitude;
    boolean pointed;
    Button boton;
    String LAT_KEY = "latitud_mapa";
    String LON_KEY = "longitud_mapa";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_map);

        pointed=false;
        boton=findViewById(R.id.pointButton);
        Fragment fragment = new MapFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .commit();

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

    }

    public void onPointed(double lat, double longt){
        pointed=true;
        latitude=lat;
        longitude=longt;
        Log.d("TAG", "Selecionadas las coordendas: " + lat + " : " + longt);
    }
}