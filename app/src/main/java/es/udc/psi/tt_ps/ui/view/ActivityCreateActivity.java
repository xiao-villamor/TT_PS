package es.udc.psi.tt_ps.ui.view;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import es.udc.psi.tt_ps.R;
import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.databinding.ActivityCreateBinding;
import es.udc.psi.tt_ps.domain.activity.createActivityUseCase;


public class ActivityCreateActivity extends AppCompatActivity {

    private ActivityCreateBinding binding;
    private List<String> selectedTags=null;
    private double latitude;
    private double longitude;
    String LAT_KEY = "latitud_mapa";
    String LON_KEY = "longitud_mapa";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        latitude=0;
        longitude=0;
        binding.createActivity.setOnClickListener(v -> {

            if(validate()){
                try {
                    Result<Object, Exception> res = createActivityUseCase.createAcyivity(
                            binding.activityTitle.getText().toString(), binding.activityDescription.getText().toString(),
                            Date.valueOf(binding.activityStart.getText().toString()), Date.valueOf(binding.activityEnd.getText().toString()),
                            user.getUid(), new PointF((float)latitude, (float) longitude), selectedTags);

                    if(res.exception!=null){
                        Log.d("TAG", res.exception.toString());
                        Toast.makeText(getApplicationContext(), "Activity cannot be created", Toast.LENGTH_SHORT).show();
                    }else{
                        Log.d("TAG", "Actividad creada correctamente");
                        Toast.makeText(getApplicationContext(), "activity created successfully", Toast.LENGTH_SHORT).show();
                        Intent userProfileIntent = new Intent(this, MainActivity.class);
                        startActivity(userProfileIntent);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });


        binding.activityTag.setOnClickListener(v -> {
            selectedTags = new ArrayList();
            showTagsChooser();
        });

        binding.newActivityMap.setOnClickListener(v -> {
            Intent mapIntent = new Intent(this, CreateActivityMap.class);
            startMapForCoordinates.launch(mapIntent);
            //startActivity(mapIntent);
        });


    }


    private void showTagsChooser(){
        AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        dialogo.setTitle("Choose the tags");
        //Array con los posibles intereses
        String[] interests=getResources().getStringArray(R.array.interests_array);


        dialogo.setMultiChoiceItems(interests, null,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which,
                                        boolean isChecked) {
                        if (isChecked) {
                            selectedTags.add(interests[which]);

                        } else if (selectedTags.contains(interests[which])) {
                            // Else, if the item is already in the array, remove it
                            selectedTags.remove(interests[which]);
                        }
                        Log.d("_TAG", selectedTags.toString());
                    }
                }
        );

        dialogo.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("_TAG", "Dialogo aceptado");
                if(selectedTags.isEmpty()){
                    selectedTags=null;
                }
            }
        });
        dialogo.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("_TAG", "Dialogo cancelado");
                selectedTags=null;
                dialogInterface.dismiss();
            }
        });
        AlertDialog alert = dialogo.create();
        alert.show();
    }


    ActivityResultLauncher<Intent> startMapForCoordinates = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                    Intent data = result.getData();
                    Bundle bundle = data.getExtras();
                    if (bundle!= null) {

                        latitude = bundle.getDouble(LAT_KEY, 0);
                        longitude = bundle.getDouble(LON_KEY, 0);
                        Log.d("TAG", "Coordenadas recividas " +  latitude + " : " + longitude);
                    }
                }
            }
    );


    private boolean validate(){
        return val_title() && val_description() && val_startDate() && val_endDate() && val_duration() && val_tags() && val_location();

    }


    private boolean val_title(){
        String titulo = binding.activityTitle.getText().toString();
        if(titulo.isEmpty()){
            Toast.makeText(getApplicationContext(), "Title cannot be empty", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Actividad no creada por titulo no indicado");
            return false;
        }
        return true;
    }

    private boolean val_description(){
        String description = binding.activityDescription.getText().toString();
        if(description.isEmpty()){
            Toast.makeText(getApplicationContext(), "Description cannot be empty", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Actividad no creada por description no indicada");
            return false;
        }
        return true;
    }

    private boolean val_startDate(){

        String date = binding.activityStart.getText().toString();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        if(date.isEmpty()){
            Toast.makeText(getApplicationContext(), "Start date cannot be empty", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Actividad no creada por fecha de inicio no indicada");
            return false;
        }

        try{
            java.util.Date d = formatter.parse(date);
            return true;

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Incorret start date", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Actividad no creada por fecha de inicio incorrecta " + e.getMessage());
            return false;
        }


    }


    private boolean val_endDate(){

        String date = binding.activityEnd.getText().toString();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        if(date.isEmpty()){
            Toast.makeText(getApplicationContext(), "End date cannot be empty", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Actividad no creada por fecha de fin no indicada");
            return false;
        }

        try{
            java.util.Date d = formatter.parse(date);
            return true;
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Incorret end date", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Actividad no creada por fecha de fin incorrecta " + e.getMessage());
            return false;
        }

    }


    private boolean val_duration(){
        String startDate = binding.activityStart.getText().toString();
        String endDate = binding.activityEnd.getText().toString();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        try{
            java.util.Date start_d = formatter.parse(startDate);
            java.util.Date end_d = formatter.parse(endDate);

            if(!start_d.before(end_d)){
                Toast.makeText(getApplicationContext(), "Start date has to ve previous to the end date", Toast.LENGTH_SHORT).show();
                Log.d("TAG", "Actividad no creada por fechas no coherentes");
                return false;
            }else{
                return true;
            }

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Incorret end or start date format", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Actividad no creada por mal formato de fechas " + e.getMessage());
            return false;
        }

    }


    private boolean val_tags(){

        if(selectedTags==null || selectedTags.isEmpty()){
            Toast.makeText(getApplicationContext(), "Activity has to be tagged", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Actividad no creada por tener tags asociados ");
            return false;
        }else{
            return true;
        }
    }

    private boolean val_location(){

        if(latitude==0 && longitude==0){
            Toast.makeText(getApplicationContext(), "Activity has to have a location", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Actividad no creada por no tener una localizacion asociada ");
            return false;
        }else{
            return true;
        }
    }


}