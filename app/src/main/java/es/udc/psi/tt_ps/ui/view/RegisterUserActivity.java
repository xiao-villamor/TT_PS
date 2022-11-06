package es.udc.psi.tt_ps.ui.view;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.icu.text.UnicodeSetIterator;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.udc.psi.tt_ps.R;
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

            if(validate()){
                try {
                    Log.d("TAG", "Imagen PostValidacion: " + file.getPath());

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
            }


        });


        binding.imageReg.setOnClickListener(v -> {
            chooseImg();
        });


    }

    private boolean validate(){
        return val_name() && val_surname() && val_email() && val_password() && val_date() && val_phone();

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

        String date = binding.birthDateReg.getText().toString();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        try{
            java.util.Date d = formatter.parse(date);
            //java.sql.Date sql = new java.sql.Date(parsed.getTime());
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Incorret date", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Cuenta no creada por fecha no incorrecta " + e.getMessage());
            return false;
        }

        if(date.isEmpty()){
            Toast.makeText(getApplicationContext(), "Date cannot be empty", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "Cuenta no creada por fecha no indicado");
            return false;
        }else{
            return true;
        }

    }

    public static String getRealPathFromDocumentUri(Context context, Uri uri){
        String filePath = "";

        Pattern p = Pattern.compile("(\\d+)$");
        Matcher m = p.matcher(uri.toString());
        if (!m.find()) {
            return filePath;
        }
        String imgId = m.group();

        String[] column = { MediaStore.Images.Media.DATA };
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ imgId }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();

        return filePath;
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
                        file= new File(getRealPathFromDocumentUri(this,imgUri));
                        Log.d("TAG", "Imagen seleccionada: " + file.getPath());
                        Log.d("TAG", "Imagen Sin subString: " + imgUri.getPath());
                        //binding.imageViewReg.setImageURI(imgUri);
                    }
                }
            }
    );




}