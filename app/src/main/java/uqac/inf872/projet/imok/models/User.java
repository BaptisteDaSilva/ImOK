package uqac.inf872.projet.imok.models;

import android.databinding.Bindable;
import android.support.annotation.Nullable;

import uqac.inf872.projet.imok.BR;

public class User extends Model {

    private String uid;
    private String username;
    private String email;
    @Nullable
    private String urlPicture;

    public User() {
    }

    public User(String uid, String username, String email, @Nullable String urlPicture) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.urlPicture = urlPicture;
    }

    // --- GETTERS ---
    @Bindable
    public String getUid() {
        return uid;
    }

    @Bindable
    public String getUsername() {
        return username;
    }

    // --- SETTERS ---
    public void setUsername(String username) {
        this.username = username;
        notifyPropertyChanged(BR.username);
    }

    @Bindable
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        notifyPropertyChanged(BR.email);
    }

    @Nullable
    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(@Nullable String urlPicture) {
        this.urlPicture = urlPicture;
        notifyPropertyChanged(BR.urlPicture);
    }
}
