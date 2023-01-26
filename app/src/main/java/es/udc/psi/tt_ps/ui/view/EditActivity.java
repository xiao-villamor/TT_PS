package es.udc.psi.tt_ps.ui.view;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.GeoPoint;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import es.udc.psi.tt_ps.R;
import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.databinding.ActivityEditBinding;
import es.udc.psi.tt_ps.domain.activity.editActivityInfoUseCase;
import es.udc.psi.tt_ps.domain.user.uploadActivityPicUseCase;
import es.udc.psi.tt_ps.ui.viewmodel.ListActivities;

public class EditActivity extends AppCompatActivity{

    private ActivityEditBinding binding;
    View view;
    ListActivities activitiesList;
    ListActivities new_activitiesList;
    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy  mm:ss");
    String image=null;
    Uri uriImage=null;
    String LAT_KEY = "latitud_mapa";
    String LON_KEY = "longitud_mapa";
    boolean called=false;
    ProgressDialog progressDialog=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditBinding.inflate(getLayoutInflater());
        view = binding.getRoot();
        setContentView(view);
        if(!called){
            called=true;
            progressDialog = new ProgressDialog(this);
            Bundle extras = getIntent().getExtras();
            activitiesList= (ListActivities) extras.get("activity");
            GeoPoint coord = new GeoPoint(extras.getDouble("latitud"),extras.getDouble("longitud"));
            activitiesList.setLocation(coord);

            new_activitiesList=activitiesList;
            image=new_activitiesList.getActivityImage();
        }


        init();
        setbuttons();
    }

    public void init(){
        if (new_activitiesList.getActivityImage()!=null)
        {
            Glide.with(binding.cardMedia.getContext()).load(new_activitiesList.getActivityImage()).into(binding.cardMedia);
        }else
        {
            Glide.with(binding.cardMedia.getContext()).load(R.drawable.ic_launcher_background).into(binding.cardMedia);
        }


        binding.etTitle.setText(new_activitiesList.getTitle());
        binding.etStartDate.setText(new_activitiesList.getStart_date().toString());
        binding.etEndDate.setText(new_activitiesList.getEnd_date().toString());
        binding.etDescription.setText(new_activitiesList.getDescription());
        showMap();
    }

    public void setbuttons(){
        binding.cancelEdit.setOnClickListener(view1 -> {
            showCancelDialog();
        });

        binding.aceptEdit.setOnClickListener(view1 -> {
            if(validate()){
                showAceptDialog();
            }
        });

        binding.etStartDate.setOnClickListener(view1 -> {
            showStartDateDialog();
        });

        binding.etEndDate.setOnClickListener(view1 -> {
            showEndDateDialog();
        });

        binding.butonNewLocation.setOnClickListener(view1 -> {
            Intent mapIntent = new Intent(this, CreateActivityMap.class);
            startMapForCoordinates.launch(mapIntent);
        });

        binding.buttonImage.setOnClickListener(view1 -> {
            //aplyChanges();
            chooseImg();
        });
    }



    ActivityResultLauncher<Intent> startMapForCoordinates = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                    Intent data = result.getData();
                    Bundle bundle = data.getExtras();
                    if (bundle!= null) {
                        GeoPoint location = new GeoPoint(bundle.getDouble(LAT_KEY),bundle.getDouble(LON_KEY));
                        new_activitiesList.setLocation(location);
                        Log.d("TAG", "Coordenadas recividas " +  bundle.getDouble(LAT_KEY) + " : " + bundle.getDouble(LON_KEY));
                        called=true;
                        showMap();
                    }
                }
            }
    );


    ActivityResultLauncher<Intent> my_startActivityForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                    Intent data = result.getData();
                    if(data!=null){
                        called=true;
                        uriImage=data.getData();
                        image=uriImage.toString();
                        Log.d("TAG", image);
                        //init();

                    }
                }
            }
    );


    private void showStartDateDialog(){
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                TimePickerDialog.OnTimeSetListener timeSetListener=new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        Date start_d=calendar.getTime();
                        new_activitiesList.setStart_date(start_d);
                        binding.etStartDate.setText(start_d.toString());
                        Log.d("TAG", "Fecha seleccionada: " + start_d.toString());
                    }
                };
                TimePickerDialog t = new TimePickerDialog(EditActivity.this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                t.show();


            }
        };
        DatePickerDialog d = new DatePickerDialog(EditActivity.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        d.show();
    }

    private void showEndDateDialog(){
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                TimePickerDialog.OnTimeSetListener timeSetListener=new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        Date end_d=calendar.getTime();
                        new_activitiesList.setEnd_date(end_d);
                        binding.etEndDate.setText(end_d.toString());
                        Log.d("TAG", "Fecha seleccionada: " + end_d.toString());
                    }
                };
                TimePickerDialog t = new TimePickerDialog(EditActivity.this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                t.show();


            }
        };
        DatePickerDialog d = new DatePickerDialog(EditActivity.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        d.show();
    }

    private void showCancelDialog(){
        AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        dialogo.setTitle("Cancel");
        dialogo.setMessage("Do you want to undo the modifications?");

        dialogo.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("TAG", "Cancel-cancel");
                onBackPressed();
            }
        });
        dialogo.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("TAG", "cancel-back");
            }
        });
        AlertDialog alert = dialogo.create();
        alert.show();
    }

    private void showAceptDialog(){
        AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        dialogo.setTitle("Acept");
        dialogo.setMessage("Do you want to save the modifications?");

        dialogo.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                updateActivity();
            }
        });
        dialogo.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("TAG", "acept-cancel");
            }
        });
        AlertDialog alert = dialogo.create();
        alert.show();
    }

    private void updateActivity(){


            Log.d("TAG", "Comienza la actualizacion");
            //progressDialog.setTitle("Uploading image");
            //progressDialog.show();
            //Upload the image and get the URL
            if(uriImage!=null){
                try {

                    Result<String, Exception> resPic= uploadActivityPicUseCase.uploadActivityPic(activitiesList.getActivityId(), compress());
                    if(resPic.exception!=null){
                        Log.d("TAG", resPic.exception.toString());
                        Toast.makeText(getApplicationContext(), "The new picture cannot be updated", Toast.LENGTH_SHORT).show();
                    }else{
                        new_activitiesList.setActivityImage(resPic.data);
                        Log.d("TAG", "New uri: " + new_activitiesList.getActivityImage());
                    }

                } catch (InterruptedException | TimeoutException | ExecutionException | FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            //Set values
            aplyChanges();

            //Lanzar peticion BBDD
            try {
                Result<Object, Exception> res = editActivityInfoUseCase.updateEditedActivity(new_activitiesList);

                if(res.exception!=null){
                    Log.d("TAG", res.exception.toString());
                    Toast.makeText(getApplicationContext(), "Activity cannot be updated", Toast.LENGTH_SHORT).show();
                }else{
                    Log.d("TAG", "Actividad actualizada correctamente");
                    Toast.makeText(getApplicationContext(), "activity updated successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("newActivity", new_activitiesList);
                    intent.putExtra("latitud",new_activitiesList.getLocation().getLatitude());
                    intent.putExtra("longitud",new_activitiesList.getLocation().getLongitude());
                    setResult(RESULT_OK, intent);
                    finish();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }



    }


    private void aplyChanges(){

        new_activitiesList.setTitle(binding.etTitle.getText().toString());
        new_activitiesList.setDescription(binding.etDescription.getText().toString());
    }

    private void showMap(){
        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.edit_activity_map);


        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {

                MarkerOptions markerOptions = new MarkerOptions();
                LatLng coord = new LatLng(new_activitiesList.getLocation().getLatitude(), new_activitiesList.getLocation().getLongitude());
                markerOptions.position(coord);
                markerOptions.title(new_activitiesList.getTitle());
                googleMap.clear();
                googleMap.addMarker(markerOptions);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coord, 14));



            }
        });

    }

    private byte[] compress(){
        if(uriImage==null || uriImage==Uri.parse("")){
            return null;
        }else{
            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uriImage);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("TAG", "No se pudo obtener el bitmap de la imagen");
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            //here you can choose quality factor in third parameter(ex. i choosen 25)
            bmp.compress(Bitmap.CompressFormat.JPEG, 20, baos);

            return baos.toByteArray();
        }

    }

    private void chooseImg(){

        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        my_startActivityForResult.launch(i);
    }


    private boolean validate(){
        return val_title() && val_description()  && val_duration();

    }


    private boolean val_title(){
        String titulo = binding.etTitle.getText().toString();
        if(titulo.isEmpty()){
            Toast.makeText(getApplicationContext(), "Title cannot be empty", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Actividad no creada por titulo no indicado");
            return false;
        }
        return true;
    }

    private boolean val_description(){
        String description = binding.etDescription.getText().toString();
        if(description.isEmpty()){
            Toast.makeText(getApplicationContext(), "Description cannot be empty", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Actividad no creada por description no indicada");
            return false;
        }
        return true;
    }



    private boolean val_duration(){
        Log.d("TAG", "Duration: " + new_activitiesList.getStart_date().toString() + " " + new_activitiesList.getEnd_date().toString());
        if(!new_activitiesList.getStart_date().before(new_activitiesList.getEnd_date())){
            Toast.makeText(getApplicationContext(), "Start date has to ve previous to the end date", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Actividad no creada por fechas no coherentes");
            return false;
        }else{
            return true;
        }

    }





}