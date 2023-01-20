package es.udc.psi.tt_ps.ui.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import es.udc.psi.tt_ps.R;
import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.data.model.UserModel;
import es.udc.psi.tt_ps.databinding.ActivityEditUserBinding;
import es.udc.psi.tt_ps.domain.activity.editActivityInfoUseCase;
import es.udc.psi.tt_ps.domain.activity.editUserInfoUseCase;
import es.udc.psi.tt_ps.domain.user.getUserInfoUseCase;
import es.udc.psi.tt_ps.domain.user.loginUserUseCase;
import es.udc.psi.tt_ps.ui.adapter.tagAdapter;

public class EditUser extends AppCompatActivity {

    private ActivityEditUserBinding binding;
    private String uuid;
    private UserModel newUser;
    List<String> selectedItems = new ArrayList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityEditUserBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Bundle extras = getIntent().getExtras();
        uuid = extras.getString("uuid");
        setInfo();

        binding.buttonInterests.setOnClickListener(v -> {
            mostrarDialogo();
        });

        binding.cancelEditUser.setOnClickListener(view1 -> {
            showCancelDialog();
        });

        binding.aceptEditUser.setOnClickListener(view1 -> {
            if(validate()){
                showAceptDialog();
            }
        });

        //aplycChanges

    }


    void setInfo(){

        try {
            //usuario válido -> email: dev_m@mail.com    password: 123456
            Result<UserModel, Exception> res = getUserInfoUseCase.getInfo(uuid);
            if(res.exception != null){
                Log.d("TAG", res.exception.toString());
                onDestroy();
            }else{
                Log.d("TAG", "Usuario ontenido");
                UserModel user = res.data;
                newUser=user;


                try {
                    Glide.with(this).load(user.getProfilePic()).into(binding.editProfilePic);
                }catch (Exception e){
                    Log.d("_TAG","no profile pic");
                }

                binding.etName.setText(user.getName());
                binding.etSurname.setText(user.getSurname());
                binding.etDescription.setText(user.getDescription());
                binding.etPhone.setText(user.getPhone());
                tagAdapter tagAdapter = new tagAdapter(user.getInterests().toArray(new String[0]));
                binding.interestsGrid.setAdapter(tagAdapter);

            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
                newUser.setInterests(selectedItems);
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
                updateUser();
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


    void updateUser(){

        aplyChanges();
        try {
            Result<Object, Exception> res = editUserInfoUseCase.updateEditedUser(newUser);

            if(res.exception!=null){
                Log.d("TAG", res.exception.toString());
                Toast.makeText(getApplicationContext(), "User cannot be updated", Toast.LENGTH_SHORT).show();
            }else{
                Log.d("TAG", "Usuario actualizado correctamente");
                Toast.makeText(getApplicationContext(), "user updated successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("edited", true);
                setResult(RESULT_OK, intent);
                finish();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    void aplyChanges(){
        newUser.setName(binding.etName.getText().toString());
        newUser.setSurname(binding.etSurname.getText().toString());
        newUser.setDescription(binding.etDescription.getText().toString());
        newUser.setPhone(binding.etPhone.getText().toString());
    }


    private boolean validate(){
        return val_name() && val_surname()  && val_phone();

    }

    private boolean val_name(){

        String name = binding.etName.getText().toString();
        if(name.isEmpty()){
            Toast.makeText(getApplicationContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Cuenta no creada por nombre no indicado");
            return false;
        }
        return true;
    }

    private boolean val_surname(){

        String surname = binding.etSurname.getText().toString();
        if(surname.isEmpty()){
            Toast.makeText(getApplicationContext(), "Surname cannot be empty", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Cuenta no creada por apellido no indicado");
            return false;
        }else{
            return true;
        }
    }


    private boolean val_phone(){

        String phone = binding.etPhone.getText().toString();
        if(phone.isEmpty()){
            Toast.makeText(getApplicationContext(), "Phone cannot be empty", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Cuenta no creada por telefono no indicado");
            return false;
        }else{
            return true;
        }
    }



}