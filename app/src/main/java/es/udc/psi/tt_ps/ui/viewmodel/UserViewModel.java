package es.udc.psi.tt_ps.ui.viewmodel;


import es.udc.psi.tt_ps.ui.adapter.ListUsersAdapter;

public class UserViewModel {

    ListUsersAdapter listUserAdapter;

    public UserViewModel(ListUsersAdapter listUsersAdapter) {
        this.listUserAdapter = listUsersAdapter;
    }
}
