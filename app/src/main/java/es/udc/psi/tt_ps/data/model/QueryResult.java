package es.udc.psi.tt_ps.data.model;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.auth.User;

public class QueryResult<T, E > {
    public T data;
    public E cursor;
}