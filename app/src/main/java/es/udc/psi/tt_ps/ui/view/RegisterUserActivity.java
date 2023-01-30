package es.udc.psi.tt_ps.ui.view;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

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


import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseUser;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import es.udc.psi.tt_ps.R;
import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.databinding.ActivityRegisterUserBinding;
import es.udc.psi.tt_ps.domain.user.createUserUseCase;

public class RegisterUserActivity extends AppCompatActivity {

    private ActivityRegisterUserBinding binding;
    private java.util.Date date=null;
    private Uri image=null;
    private List<String> selectedTags=new ArrayList();;

    ProgressDialog progressDialog=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterUserBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        //image=Uri.parse("");
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.dialog_new_user_title));

        binding.signupReg.setOnClickListener(v -> {

            if(validate()){
                try {
                    //Log.d("TAG", "Imagen PostValidacion: " + file.getPath());
                    //create arrayList

                    progressDialog.show();

                    Result<FirebaseUser, Exception> res = createUserUseCase.createUser(
                            binding.nameReg.getText().toString(), binding.emailReg.getText().toString(), binding.passwordReg.getText().toString(),
                            binding.surnameReg.getText().toString(), date, binding.phoneReg.getText().toString(),
                            compress(), null, selectedTags, 0.0f);



                    if(res.exception != null){
                        Log.d("TAG", res.exception.toString());
                        Toast.makeText(getApplicationContext(), R.string.toast_notUser, Toast.LENGTH_SHORT).show();
                        //progressDialog.dismiss();
                    } else{
                        Log.d("TAG", "Usuario creado correctamente");
                        Toast.makeText(getApplicationContext(), R.string.toast_newUser, Toast.LENGTH_SHORT).show();
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
            selectDateDialog();
        });

        binding.imageReg.setOnClickListener(v -> {
            chooseImg();
        });

        binding.interests.setOnClickListener(v -> {
            showTagsChooser();
        });


    }


    private void selectDateDialog() {
        MaterialDatePicker datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .build();
        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");

        datePicker.addOnPositiveButtonClickListener(selection -> {

            String dateS = new SimpleDateFormat("dd/MM/yyyy").format(selection);
            Log.d("TAG", "Date: " + dateS);
            binding.textDate.setText(dateS);
            date = new Date((Long) selection);

        });



    }

    private void showTagsChooser(){
        MaterialAlertDialogBuilder dialogo = new MaterialAlertDialogBuilder(this);
        dialogo.setTitle(R.string.dialog_interests_title);
        //Array con los posibles intereses
        String[] interests=getResources().getStringArray(R.array.interests_array);
        boolean[] checkedItems = new boolean[interests.length];
        //make position true if this item is in checkedItems
        Log.d("_TAG", "antes " + selectedTags.toString());
        for (int i = 0; i < interests.length; i++) {
            checkedItems[i] = selectedTags.contains(interests[i]);
        }


        dialogo.setMultiChoiceItems(interests, checkedItems,
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

        dialogo.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("_TAG", "Dialogo aceptado");
                if(selectedTags.isEmpty()){
                    selectedTags=null;
                }
                Log.d("_TAG", selectedTags.toString());
            }
        });
        dialogo.setNegativeButton(R.string.dialog_cancel,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("_TAG", "Dialogo cancelado");
                selectedTags=null;
                dialogInterface.dismiss();
            }
        });

        androidx.appcompat.app.AlertDialog alert = dialogo.create();
        alert.show();
    }



    private boolean validate(){
        return val_name() && val_surname() && val_email() && val_password() && val_date() && val_phone() && val_interests();

    }

    private boolean val_name(){

        String name = binding.nameReg.getText().toString();
        if(name.isEmpty()){
            Toast.makeText(getApplicationContext(), R.string.toast_valName, Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Cuenta no creada por nombre no indicado");
            return false;
        }
        return true;
    }

    private boolean val_surname(){

        String surname = binding.surnameReg.getText().toString();
        if(surname.isEmpty()){
            Toast.makeText(getApplicationContext(), R.string.toast_valSurname, Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getApplicationContext(), R.string.toast_valEmail_empty, Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Cuenta no creada por nombre no indicada ");
            return false;
        }else if(!email.matches(emailPattern)){
            Toast.makeText(getApplicationContext(), R.string.toast_valEmail_invalid, Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Cuenta no creada por formta de email invalido");
            return false;
        }else{
            return true;
        }
    }

    private boolean val_phone(){

        String phone = binding.phoneReg.getText().toString();
        if(phone.isEmpty()){
            Toast.makeText(getApplicationContext(), R.string.toast_valPhone, Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Cuenta no creada por telefono no indicado");
            return false;
        }else{
            return true;
        }
    }

    private boolean val_password(){

        String password = binding.passwordReg.getText().toString();
        if(password.isEmpty()){
            Toast.makeText(getApplicationContext(), R.string.toast_valPass_empty, Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Cuenta no creada por contraseña no indicado");
            return false;
        }else if(password.length()<6){
            Toast.makeText(getApplicationContext(), R.string.toast_valPass_min, Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Cuenta no creada por contraseña menor a 6 digitos");
            return false;
        }else{
            return true;
        }
    }

    private boolean val_date(){

        if(date==null){
            Toast.makeText(getApplicationContext(), R.string.toast_valDate, Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Cuenta no creada por fecha no indicado");
            return false;
        }else{
            return true;
        }

    }

    private boolean val_interests(){

        if(selectedTags==null || selectedTags.isEmpty()){
            Toast.makeText(getApplicationContext(), R.string.toast_valInterests, Toast.LENGTH_SHORT).show();
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