package es.udc.psi.tt_ps.ui.view;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import es.udc.psi.tt_ps.R;
import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.databinding.ActivityEditBinding;
import es.udc.psi.tt_ps.domain.activity.editActivityInfoUseCase;
import es.udc.psi.tt_ps.ui.viewmodel.ListActivities;

public class EditActivity extends AppCompatActivity{

    private ActivityEditBinding binding;
    View view;
    ListActivities activitiesList;
    ListActivities new_activitiesList;
    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy  mm:ss");
    Date start_d=null;
    Date end_d=null;
    GeoPoint location=null;
    String image=null;
    Uri uriImage=null;
    String LAT_KEY = "latitud_mapa";
    String LON_KEY = "longitud_mapa";
    boolean called=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditBinding.inflate(getLayoutInflater());
        view = binding.getRoot();
        setContentView(view);
        if(!called){
            called=true;
            Bundle extras = getIntent().getExtras();
            activitiesList= (ListActivities) extras.get("activity");
            GeoPoint coord = new GeoPoint(extras.getDouble("latitud"),extras.getDouble("longitud"));
            activitiesList.setLocation(coord);

            new_activitiesList=activitiesList;
            start_d=new_activitiesList.getStart_date();
            end_d=new_activitiesList.getEnd_date();
            location=new_activitiesList.getLocation();
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
        binding.etStartDate.setText(simpleDateFormat.format(start_d));
        binding.etEndDate.setText(simpleDateFormat.format(end_d));
        binding.etDescription.setText(new_activitiesList.getDescription());
        showMap();
    }

    public void setbuttons(){
        binding.cancelEdit.setOnClickListener(view1 -> {
            showCancelDialog();
        });

        binding.aceptEdit.setOnClickListener(view1 -> {
            showAceptDialog();
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
            aplyChanges();
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
                        location = new GeoPoint(bundle.getDouble(LAT_KEY),bundle.getDouble(LON_KEY));
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
                        uriImage=data.getData();
                        image=uriImage.toString();
                        Log.d("TAG", image.toString());
                        called=true;
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
                        start_d=calendar.getTime();
                        new_activitiesList.setStart_date(start_d);
                        binding.etStartDate.setText(simpleDateFormat.format(start_d));
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
                        end_d=calendar.getTime();
                        new_activitiesList.setEnd_date(end_d);
                        binding.tvEndDate.setText(simpleDateFormat.format(end_d));
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
                Log.d("TAG", "Comienza la actualizacion");
                //SET VALUES
                aplyChanges();

                //LANZAR PETICION BBDD
                try {
                    Result<Object, Exception> res = editActivityInfoUseCase.updateEditedActivity(new_activitiesList);

                    if(res.exception!=null){
                        Log.d("TAG", res.exception.toString());
                        Toast.makeText(getApplicationContext(), "Activity cannot be updated", Toast.LENGTH_SHORT).show();
                    }else{
                        Log.d("TAG", "Actividad actualizada correctamente");
                        Toast.makeText(getApplicationContext(), "activity updated successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.putExtra("newActivity", activitiesList);
                        intent.putExtra("latitud",activitiesList.getLocation().getLatitude());
                        intent.putExtra("longitud",activitiesList.getLocation().getLongitude());
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


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


    private void aplyChanges(){

        new_activitiesList.setTitle(binding.etTitle.getText().toString());
        new_activitiesList.setStart_date(start_d);
        new_activitiesList.setEnd_date(end_d);
        new_activitiesList.setDescription(binding.etDescription.getText().toString());
        new_activitiesList.setLocation(location);

    }

    private void showMap(){
        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.edit_activity_map);


        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {

                MarkerOptions markerOptions = new MarkerOptions();
                LatLng coord = new LatLng(location.getLatitude(), location.getLongitude());
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





}