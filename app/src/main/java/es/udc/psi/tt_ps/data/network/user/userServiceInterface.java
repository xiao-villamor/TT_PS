package es.udc.psi.tt_ps.data.network.user;


import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import es.udc.psi.tt_ps.data.model.Result;

import es.udc.psi.tt_ps.data.model.UserModel;

public interface userServiceInterface {
    public void createUser(String email , String password , UserModel user, File pic) throws ExecutionException, InterruptedException, TimeoutException;
    public void loginUser(String email, String password) throws ExecutionException, InterruptedException, TimeoutException;
    public void updateUser(UserModel user);
    public UserModel getUser(String uuid) throws ExecutionException, InterruptedException, TimeoutException;
    public List<UserModel> getUserByUsername(String username) throws ExecutionException, InterruptedException, TimeoutException;
    public void deleteUser();
    public void UpdateUserDetails(String uuid, UserModel user);
    public void updateUserEmail(String email);
    public void updateUserPassword(String password);
    public String uploadProfilePic(String uuid, File image) throws FileNotFoundException, ExecutionException, InterruptedException, TimeoutException;
}
