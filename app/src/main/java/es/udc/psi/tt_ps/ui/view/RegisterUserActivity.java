package es.udc.psi.tt_ps.ui.view;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseUser;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import es.udc.psi.tt_ps.R;
import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.databinding.ActivityRegisterUserBinding;
import es.udc.psi.tt_ps.domain.user.createUserUseCase;

public class RegisterUserActivity extends AppCompatActivity {

    private ActivityRegisterUserBinding binding;
    private java.util.Date date=null;
    private Uri image=null;
    private List<String> selectedItems=null;
    ProgressDialog progressDialog=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterUserBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        //image=Uri.parse("");
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Creating user");

        binding.signupReg.setOnClickListener(v -> {

            if(validate()){
                try {
                    //Log.d("TAG", "Imagen PostValidacion: " + file.getPath());
                    //create arrayList
                    ArrayList<Float> ratings = new ArrayList<>();
                    ratings.add(0f);
                    progressDialog.show();

                    Result<FirebaseUser, Exception> res = createUserUseCase.createUser(
                            binding.nameReg.getText().toString(), binding.emailReg.getText().toString(), binding.passwordReg.getText().toString(),
                            binding.surnameReg.getText().toString(), date, binding.phoneReg.getText().toString(),
                            compress(), null, selectedItems, ratings);



                    if(res.exception != null){
                        Log.d("TAG", res.exception.toString());
                        Toast.makeText(getApplicationContext(), "Cannot create the user", Toast.LENGTH_SHORT).show();
                        //progressDialog.dismiss();
                    } else{
                        Log.d("TAG", "Usuario creado correctamente");
                        Toast.makeText(getApplicationContext(), "User created successfuly", Toast.LENGTH_SHORT).show();
                        Intent userProfileIntent = new Intent(this, MainActivity.class);
                        startActivity(userProfileIntent);
                    }
                    this.finish();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });

        binding.buttonDate.setOnClickListener(v -> {
            showStartDateDialog();
        });

        binding.imageReg.setOnClickListener(v -> {
            chooseImg();
        });

        binding.interests.setOnClickListener(v -> {
            selectedItems = new ArrayList();
            mostrarDialogo();
        });


    }

    private void showStartDateDialog(){
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                date=calendar.getTime();
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy");
                binding.textDate.setText(simpleDateFormat.format(date));
                Log.d("TAG", "Fecha seleccionada: " + date.toString());

            }
        };
        DatePickerDialog d = new DatePickerDialog(RegisterUserActivity.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        d.show();


    }

    private void mostrarDialogo(){
        AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        dialogo.setTitle("Choose the interests");
        //Array con los posibles intereses
        String[] interests=getResources().getStringArray(R.array.interests_array);


        dialogo.setMultiChoiceItems(interests, null,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which,
                                        boolean isChecked) {
                        if (isChecked) {
                           selectedItems.add(interests[which]);

                        } else if (selectedItems.contains(interests[which])) {
                            // Else, if the item is already in the array, remove it
                            selectedItems.remove(interests[which]);
                        }
                        Log.d("_TAG", selectedItems.toString());
                    }
                }
        );

        dialogo.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("_TAG", "Dialogo aceptado");
                if(selectedItems.isEmpty()){
                    selectedItems=null;
                }
            }
        });
        dialogo.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("_TAG", "Dialogo cancelado");
                selectedItems=null;
                dialogInterface.dismiss();
            }
        });
        AlertDialog alert = dialogo.create();
        alert.show();
    }

    private boolean validate(){
        return val_name() && val_surname() && val_email() && val_password() && val_date() && val_phone() && val_interests();

    }

    private boolean val_name(){

        String name = binding.nameReg.getText().toString();
        if(name.isEmpty()){
            Toast.makeText(getApplicationContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Cuenta no creada por nombre no indicado");
            return false;
        }
        return true;
    }

    private boolean val_surname(){

        String surname = binding.surnameReg.getText().toString();
        if(surname.isEmpty()){
            Toast.makeText(getApplicationContext(), "Surname cannot be empty", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Cuenta no creada por apellido no indicado");
            return false;
        }else{
            return true;
        }
    }


    private boolean val_email(){

        String email = binding.emailReg.getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if(email.isEmpty()){
            Toast.makeText(getApplicationContext(), "Email cannot be empty", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Cuenta no creada por nombre no indicada ");
            return false;
        }else if(!email.matches(emailPattern)){
            Toast.makeText(getApplicationContext(), "Email format invalid", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Cuenta no creada por formta de email invalido");
            return false;
        }else{
            return true;
        }
    }

    private boolean val_phone(){

        String phone = binding.phoneReg.getText().toString();
        if(phone.isEmpty()){
            Toast.makeText(getApplicationContext(), "Phone cannot be empty", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Cuenta no creada por telefono no indicado");
            return false;
        }else{
            return true;
        }
    }

    private boolean val_password(){

        String password = binding.passwordReg.getText().toString();
        if(password.isEmpty()){
            Toast.makeText(getApplicationContext(), "Password cannot be empty", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Cuenta no creada por contraseña no indicado");
            return false;
        }else if(password.length()<6){
            Toast.makeText(getApplicationContext(), "Password must have min 6", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Cuenta no creada por contraseña menor a 6 digitos");
            return false;
        }else{
            return true;
        }
    }

    private boolean val_date(){

        if(date==null){
            Toast.makeText(getApplicationContext(), "Date cannot be empty", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Cuenta no creada por fecha no indicado");
            return false;
        }else{
            return true;
        }

    }

    private boolean val_interests(){

        if(selectedItems==null || selectedItems.isEmpty()){
            Toast.makeText(getApplicationContext(), "Must be chosen at least one interest", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Cuenta no creada por no haber escogido minimo un tag");
            return false;
        }else{
            return true;
        }

    }


    private byte[] compress(){
        if(image==null || image==Uri.parse("")){
            return null;
        }else{
            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), image);
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

    ActivityResultLauncher<Intent> my_startActivityForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                    Intent data = result.getData();
                    if(data!=null){
                        image=data.getData();
                        Log.d("TAG", image.toString());

                    }
                }
            }
    );




}