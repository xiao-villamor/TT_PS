package es.udc.psi.tt_ps.ui.view;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;


import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.sql.Date;

import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.databinding.ActivityRegisterUserBinding;
import es.udc.psi.tt_ps.domain.user.createUserUseCase;
import es.udc.psi.tt_ps.domain.user.loginUserUseCase;

public class RegisterUserActivity extends AppCompatActivity {

    private ActivityRegisterUserBinding binding;
    private File file=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterUserBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        binding.signupReg.setOnClickListener(v -> {

            try {
                Result<FirebaseUser, Exception> res = createUserUseCase.createUser(
                        binding.nameReg.getText().toString(), binding.emailReg.getText().toString() , binding.passwordReg.getText().toString(),
                        binding.surnameReg.getText().toString(), Date.valueOf(binding.birthDateReg.getText().toString()), binding.phoneReg.getText().toString(),
                        file, null, null);

                if(res.exception != null){
                    Log.d("TAG", res.exception.toString());
                }
                else{
                    Log.d("TAG", "Usuario creado correctamente");
                    Intent userProfileIntent = new Intent(this, MainActivity.class);
                    startActivity(userProfileIntent);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });


        binding.imageReg.setOnClickListener(v -> {
            chooseImg();
        });


    }

    private void chooseImg(){
        /*
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        my_startActivityForResult.launch(i);

         */

        Intent i = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        my_startActivityForResult.launch(i);
    }

    ActivityResultLauncher<Intent> my_startActivityForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                    Intent data = result.getData();
                    if(data!=null){
                        Uri imgUri=data.getData();
                        /*
                        String[] filePathColumn = { MediaStore.Images.Media.DATA };
                        Cursor cursor = getContentResolver().query(imgUri,
                                filePathColumn, null, null, null);
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String picturePath = cursor.getString(columnIndex);
                        file= new File(picturePath);

                         */
                        file= new File(String.valueOf(imgUri));
                        Log.d("TAG", "Imagen seleccionada: " + String.valueOf(imgUri));
                        //binding.imageViewReg.setImageURI(imgUri);
                    }
                }
            }
    );




}