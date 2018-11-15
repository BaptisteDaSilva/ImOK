package uqac.inf872.projet.imok.models;

import android.support.annotation.Nullable;

/**
 * Created by Philippe on 30/01/2018.
 */

public class User {

    private String uid;
    private String username;
    private Boolean isMentor;
    @Nullable
    private String urlPicture;

    public User() {
    }

    public User(String uid, String username, @Nullable String urlPicture) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.isMentor = false;
    }

    // --- GETTERS ---
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    // --- SETTERS ---
    public void setUsername(String username) {
        this.username = username;
    }

    @Nullable
    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(@Nullable String urlPicture) {
        this.urlPicture = urlPicture;
    }

    public Boolean getIsMentor() {
        return isMentor;
    }

    public void setIsMentor(Boolean mentor) {
        isMentor = mentor;
    }
}
