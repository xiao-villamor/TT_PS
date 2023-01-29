package es.udc.psi.tt_ps.ui.view;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import es.udc.psi.tt_ps.R;
import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.data.model.UserModel;
import es.udc.psi.tt_ps.databinding.ActivityEditUserBinding;
import es.udc.psi.tt_ps.domain.user.editUserInfoUseCase;
import es.udc.psi.tt_ps.domain.user.updatePasswordUseCase;
import es.udc.psi.tt_ps.domain.user.uploadUserPicUseCase;
import es.udc.psi.tt_ps.domain.user.getUserInfoUseCase;
import es.udc.psi.tt_ps.ui.adapter.tagAdapter;

public class EditUser extends AppCompatActivity {

    private ActivityEditUserBinding binding;
    private String uuid;
    private UserModel newUser;
    List<String> selectedItems = new ArrayList();
    Uri uriImage=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityEditUserBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Bundle extras = getIntent().getExtras();
        uuid = extras.getString("uuid");
        setInfo();


        binding.buttonUserImage.setOnClickListener(view1 -> {
            chooseImg();
        });

        binding.buttonInterests.setOnClickListener(v -> {
            interestsDialogo();
        });

        binding.cancelEditUser.setOnClickListener(view1 -> {
            showCancelDialog();
        });

        binding.aceptEditUser.setOnClickListener(view1 -> {
            if(validate()){
                showAceptDialog();
            }
        });

        binding.buttonNewPassword.setOnClickListener(view1 -> {
            passwordDialog();
        });

    }


    void setInfo(){

        try {
            //usuario válido -> email: dev_m@mail.com    password: 123456
            Result<UserModel, Exception> res = getUserInfoUseCase.getInfo(uuid);
            if(res.exception != null){
                Log.d("TAG", res.exception.toString());
                onDestroy();
            }else{
                Log.d("TAG", "Usuario obtenido");
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

    private void interestsDialogo(){
        AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        dialogo.setTitle(R.string.dialog_interestsTitle);
        //Array con los posibles intereses
        String[] interests=getResources().getStringArray(R.array.interests_array);
        selectedItems=new ArrayList();

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

        dialogo.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("_TAG", "Dialogo aceptado");
                if(selectedItems.isEmpty()){
                    selectedItems=null;
                }
                newUser.setInterests(selectedItems);
                tagAdapter tagAdapter = new tagAdapter(newUser.getInterests().toArray(new String[0]));
                binding.interestsGrid.setAdapter(tagAdapter);
            }
        });
        dialogo.setNegativeButton(R.string.dialog_cancel,new DialogInterface.OnClickListener() {
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


    private void passwordDialog(){
        AlertDialog.Builder buldier = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_new_password, null);
        EditText newPass=mView.findViewById(R.id.newPassword);
        EditText newPass2=mView.findViewById(R.id.newPassword2);


        buldier.setPositiveButton(R.string.dialog_acept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        buldier.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });

        buldier.setView(mView);
        AlertDialog dialog = buldier.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(newPass.getText().toString().equals(newPass2.getText().toString())){
                    if(newPass.getText().toString().length()<6){
                        Toast.makeText(EditUser.this, R.string.toast_minLengthPass, Toast.LENGTH_SHORT).show();
                    }else{
                        Log.d("TAG", "New passwords match");
                        updatePassword(newPass.getText().toString());
                        dialog.dismiss();
                    }
                }else{
                    Log.d("TAG", "New passwords do not match");
                    Toast.makeText(EditUser.this, R.string.toast_passNotMatch, Toast.LENGTH_SHORT).show();

                }
            }
        });


    }

    void updatePassword(String pass){
        try {
            Result<Object, Exception> res= updatePasswordUseCase.updateUserPassword(pass);
            if(res.exception!=null){
                Log.d("TAG", res.exception.toString());
                Toast.makeText(getApplicationContext(), R.string.toast_passNotUpdated, Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(), R.string.toast_passUpdated, Toast.LENGTH_LONG).show();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void showCancelDialog(){
        AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        dialogo.setTitle(R.string.dialog_cancel);
        dialogo.setMessage(R.string.dialog_cancelEditUser_msg);

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
        dialogo.setMessage(R.string.dialog_aceptEditUser_msg);

        dialogo.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                updateUser();
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


    void updateUser(){

        Log.d("TAG", "Comienza la actualizacion");
        //Upload the image and get the URL
        if(uriImage!=null){
            try {

                Result<String, Exception> resPic= uploadUserPicUseCase.uploadUserPic(uuid, compress());
                if(resPic.exception!=null){
                    Log.d("TAG", resPic.exception.toString());
                    Toast.makeText(getApplicationContext(), R.string.toast_notImage, Toast.LENGTH_SHORT).show();
                }else{
                    newUser.profilePic(resPic.data);
                    Log.d("TAG", "New uri: " + resPic.data);
                }

            } catch (InterruptedException | TimeoutException | ExecutionException | FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        aplyChanges();
        try {
            Result<Object, Exception> res = editUserInfoUseCase.updateEditedUser(newUser);

            if(res.exception!=null){
                Log.d("TAG", res.exception.toString());
                Toast.makeText(getApplicationContext(), R.string.toast_notUpdateUser, Toast.LENGTH_SHORT).show();
            }else{
                Log.d("TAG", "Usuario actualizado correctamente");
                Toast.makeText(getApplicationContext(), R.string.toast_updateUser, Toast.LENGTH_SHORT).show();
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


    private byte[] compress(){
        if(uriImage==null || uriImage== Uri.parse("")){
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

    ActivityResultLauncher<Intent> my_startActivityForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                    Intent data = result.getData();
                    if(data!=null){
                        uriImage=data.getData();
                        Log.d("TAG", uriImage.toString());

                    }
                }
            }
    );


    private boolean validate(){
        return val_name() && val_surname()  && val_phone();

    }

    private boolean val_name(){

        String name = binding.etName.getText().toString();
        if(name.isEmpty()){
            Toast.makeText(getApplicationContext(), R.string.toast_valName, Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Cuenta no creada por nombre no indicado");
            return false;
        }
        return true;
    }

    private boolean val_surname(){

        String surname = binding.etSurname.getText().toString();
        if(surname.isEmpty()){
            Toast.makeText(getApplicationContext(), R.string.toast_valSurname, Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Cuenta no creada por apellido no indicado");
            return false;
        }else{
            return true;
        }
    }


    private boolean val_phone(){

        String phone = binding.etPhone.getText().toString();
        if(phone.isEmpty()){
            Toast.makeText(getApplicationContext(), R.string.toast_valPhone, Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Cuenta no creada por telefono no indicado");
            return false;
        }else{
            return true;
        }
    }





}