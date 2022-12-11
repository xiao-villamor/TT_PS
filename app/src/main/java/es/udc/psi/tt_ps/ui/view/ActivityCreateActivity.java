package es.udc.psi.tt_ps.ui.view;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import es.udc.psi.tt_ps.R;
import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.databinding.ActivityCreateBinding;
import es.udc.psi.tt_ps.domain.activity.createActivityUseCase;


public class ActivityCreateActivity extends AppCompatActivity {

    private ActivityCreateBinding binding;
    private List<String> selectedTags=null;
    private double latitude;
    private double longitude;
    private java.util.Date startDate;
    private java.util.Date endDate;
    private String startHour;
    private String endHour;
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
        startDate=null;
        endDate=null;

        binding.createActivity.setOnClickListener(v -> {

            if(validate()){
                String hash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(latitude, longitude));

                try {
                    Log.d("TAG", "Geohash: "+GeoFireUtils.getGeoHashForLocation(new GeoLocation((float)latitude, (float) longitude)));
                    Result<Object, Exception> res = createActivityUseCase.createActivity(
                            String.valueOf(binding.activityTitle.getEditText().getText()), String.valueOf(binding.activityDescription.getEditText().getText()),
                            startDate, endDate,
                            user.getUid(), new GeoPoint((float)latitude, (float) longitude),hash,selectedTags);

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

        binding.buttonDate.setOnClickListener(v -> {
            selectDateDialog();
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

    private void selectDateDialog() {
        MaterialDatePicker datePicker = MaterialDatePicker.Builder.dateRangePicker()
                        .setTitleText("Select date")
                        .setSelection(new Pair(
                                MaterialDatePicker.thisMonthInUtcMilliseconds(),
                                MaterialDatePicker.todayInUtcMilliseconds()
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
        Calendar calendar = Calendar.getInstance();
        if(startDate==null) {
            Snackbar.make(binding.getRoot(), "Select a start date first", Snackbar.LENGTH_LONG).show();
        }else {
            MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(calendar.get(Calendar.HOUR_OF_DAY))
                    .setMinute(calendar.get(Calendar.MINUTE))
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
        Calendar calendar = Calendar.getInstance();
        if(startDate==null) {
            Snackbar.make(binding.getRoot(), "Select a start date first", Snackbar.LENGTH_LONG).show();
        }else {
            MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(calendar.get(Calendar.HOUR_OF_DAY))
                    .setMinute(calendar.get(Calendar.MINUTE))
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
                binding.activityDate.setText(date + " - " + date2);


            });

        }
    }

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
                        startDate=calendar.getTime();
                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy  mm:ss");
                        //binding.activityStart.setText(simpleDateFormat.format(startDate));
                        Log.d("TAG", "Fecha seleccionada: " + startDate.toString());
                    }
                };
                TimePickerDialog t = new TimePickerDialog(ActivityCreateActivity.this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                t.show();


            }
        };
        DatePickerDialog d = new DatePickerDialog(ActivityCreateActivity.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
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
                        endDate=calendar.getTime();
                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy  mm:ss");
                        //binding.activityEnd.setText(simpleDateFormat.format(endDate));
                        Log.d("TAG", "Fecha seleccionada: " + endDate.toString());
                    }
                };
                TimePickerDialog t = new TimePickerDialog(ActivityCreateActivity.this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                t.show();


            }
        };
        DatePickerDialog d = new DatePickerDialog(ActivityCreateActivity.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        d.show();
    }

    private void showTagsChooser(){
        MaterialAlertDialogBuilder dialogo = new MaterialAlertDialogBuilder(this);
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
        String titulo = String.valueOf(binding.activityTitle.getEditText().getText());
        Log.d("TAG", "Titulo: " + titulo);

        if(titulo.isEmpty()){
            Toast.makeText(getApplicationContext(), "Title cannot be empty", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Actividad no creada por titulo no indicado");
            return false;
        }
        return true;
    }

    private boolean val_description(){
        String description = String.valueOf(binding.activityDescription.getEditText().getText());
        if(description.isEmpty()){
            Toast.makeText(getApplicationContext(), "Description cannot be empty", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Actividad no creada por description no indicada");
            return false;
        }
        return true;
    }


    private boolean val_startDate(){

        if(startDate==null){
            Toast.makeText(getApplicationContext(), "Start date cannot be empty", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Actividad no creada por fecha de inicio no indicada");
            return false;
        }else{
            return true;
        }


    }


    private boolean val_endDate(){

        if(endDate==null){
            Toast.makeText(getApplicationContext(), "End date cannot be empty", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Actividad no creada por fecha de fin no indicada");
            return false;
        }else{
            return true;
        }

    }


    private boolean val_duration(){

        if(!startDate.before(endDate)){
            Toast.makeText(getApplicationContext(), "Start date has to ve previous to the end date", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Actividad no creada por fechas no coherentes");
            return false;
        }else{
            return true;
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