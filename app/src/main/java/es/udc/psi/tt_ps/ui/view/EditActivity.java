package es.udc.psi.tt_ps.ui.view;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

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
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.firestore.GeoPoint;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
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
    private java.util.Date startDate;
    private java.util.Date endDate;
    private String startHour;
    private String endHour;
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


        try {
            init();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        setbuttons();
    }

    public void init() throws ParseException {
        if (new_activitiesList.getActivityImage()!=null)
        {
            Glide.with(binding.cardMedia.getContext()).load(new_activitiesList.getActivityImage()).into(binding.cardMedia);
        }else
        {
            Glide.with(binding.cardMedia.getContext()).load(R.drawable.ic_launcher_background).into(binding.cardMedia);
        }


        binding.etTitle.setText(new_activitiesList.getTitle());
        binding.etDescription.setText(new_activitiesList.getDescription());
        String StartDate = activitiesList.getStart_date().toString();
        String EndDate = activitiesList.getEnd_date().toString();
        //convert a String that represent a date in yyyy-MM-dd HH:mm:ss z format to String with format dd/MM/yyyy HH:mm
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        Date date = sdf.parse(StartDate);
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH);
        String StartDate2 = sdf2.format(date);
        Date date2 = sdf.parse(EndDate);
        String EndDate2 = sdf2.format(date2);
        binding.etStartDate.setText(StartDate2 + " - " + EndDate2);
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
            selectDateDialog();
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


    private void selectDateDialog() {
        MaterialDatePicker datePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select date")
                .setSelection(new Pair(
                        //pair contain the start date and the end date of the listActivities
                        new_activitiesList.getStart_date().getTime(),
                        new_activitiesList.getEnd_date().getTime()
                ))
                .build();
        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");

        datePicker.addOnPositiveButtonClickListener(selection -> {

            Pair res = (Pair) selection;
            Log.d("TAG", "Fecha seleccionada: "+res.first.toString());
            String date = new SimpleDateFormat("dd/MM/yyyy").format(res.first);
            startDate = new Date((Long) res.first);
            String date2 = new SimpleDateFormat("dd/MM/yyyy").format(res.second);
            endDate =  new Date((Long) res.second);
            timePickerDialog();
        });



    }

    private void timePickerDialog() {
        //get the start time of the listActivities as date for the time picker
        Calendar startTime = Calendar.getInstance();
        startTime.setTime(activitiesList.getStart_date());

        Calendar calendar = Calendar.getInstance();
        if(startDate==null) {
            Snackbar.make(binding.getRoot(), "Select a start date first", Snackbar.LENGTH_LONG).show();
        }else {
            MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(startTime.get(Calendar.HOUR_OF_DAY))
                    .setMinute(startTime.get(Calendar.MINUTE))
                    .setTitleText("Select Start Time")
                    .build();
            timePicker.show(getSupportFragmentManager(), "tag");

            timePicker.addOnPositiveButtonClickListener(v -> {
                String time = timePicker.getHour() + ":" + timePicker.getMinute();
                SimpleDateFormat f = new SimpleDateFormat("HH:mm");
                long milliseconds = 0;
                try {
                    Date d = f.parse(time);
                    milliseconds = d.getTime();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d("TAG", "Hora seleccionada: " + time);
                startDate.setTime(startDate.getTime() + milliseconds);
                //startDate.setTime(timePicker.getHour() + timePicker.getMinute());
                Log.d("TAG", "Time: " + startDate);
                startHour = time;
                timePickerEndDialog();
            });
        }
    }

    private void timePickerEndDialog() {
        Calendar EndDate = Calendar.getInstance();
        EndDate.setTime(activitiesList.getEnd_date());
        Calendar calendar = Calendar.getInstance();
        if(startDate==null) {
            Snackbar.make(binding.getRoot(), "Select a start date first", Snackbar.LENGTH_LONG).show();
        }else {
            MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(EndDate.get(Calendar.HOUR_OF_DAY))
                    .setMinute(EndDate.get(Calendar.MINUTE))
                    .setTitleText("Select End Time")
                    .build();
            timePicker.show(getSupportFragmentManager(), "tag");

            timePicker.addOnPositiveButtonClickListener(v -> {
                String time = timePicker.getHour() + ":" + timePicker.getMinute();
                SimpleDateFormat f = new SimpleDateFormat("HH:mm");
                long milliseconds = 0;
                try {
                    Date d = f.parse(time);
                    milliseconds = d.getTime();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d("TAG", "Hora end: " + time);
                endDate.setTime(endDate.getTime() + milliseconds);
                //startDate.setTime(timePicker.getHour() + timePicker.getMinute());
                Log.d("TAG", "end: " + endDate);
                endHour = time;
                //Parse date to string in format dd/MM/yyyy HH:mm
                String date = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(startDate);
                String date2 = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(endDate);
                binding.etStartDate.setText(date + " - " + date2);


            });

        }
    }


    private void showCancelDialog(){
        AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        dialogo.setTitle(R.string.dialog_cancel);
        dialogo.setMessage(R.string.dialog_cancel_msg);

        dialogo.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("TAG", "Cancel-cancel");
                onBackPressed();
            }
        });
        dialogo.setNegativeButton(R.string.dialog_back, new DialogInterface.OnClickListener() {
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
        dialogo.setTitle(R.string.dialog_acept);
        dialogo.setMessage(R.string.dialog_acept_msg);

        dialogo.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                updateActivity();
            }
        });
        dialogo.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
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
                        Toast.makeText(getApplicationContext(), R.string.toast_notImage, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplicationContext(), R.string.toast_notUpdateActivity, Toast.LENGTH_SHORT).show();
                }else{
                    Log.d("TAG", "Actividad actualizada correctamente");
                    Toast.makeText(getApplicationContext(), R.string.toast_updateActivity, Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getApplicationContext(), R.string.toast_valTitle, Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Actividad no creada por titulo no indicado");
            return false;
        }
        return true;
    }

    private boolean val_description(){
        String description = binding.etDescription.getText().toString();
        if(description.isEmpty()){
            Toast.makeText(getApplicationContext(), R.string.toast_valDescription, Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Actividad no creada por description no indicada");
            return false;
        }
        return true;
    }



    private boolean val_duration(){
        Log.d("TAG", "Duration: " + new_activitiesList.getStart_date().toString() + " " + new_activitiesList.getEnd_date().toString());
        if(!new_activitiesList.getStart_date().before(new_activitiesList.getEnd_date())){
            Toast.makeText(getApplicationContext(), R.string.toast_valDuration, Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Actividad no creada por fechas no coherentes");
            return false;
        }else{
            return true;
        }

    }





}