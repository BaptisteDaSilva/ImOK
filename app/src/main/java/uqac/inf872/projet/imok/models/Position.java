package uqac.inf872.projet.imok.models;


import android.databinding.Bindable;

import com.google.firebase.firestore.GeoPoint;

import java.util.List;

import uqac.inf872.projet.imok.BR;

public class Position extends Model {

    private String name;
    private boolean wifi;
    private GeoPoint coordonnes;
    private int rayon;
    private List<String> ssid;
    private String userID;

    // Use by Firebase
    public Position() {
    }

    public Position(String idGPS, String name, GeoPoint coordonnes, int rayon, String userID) {
        super(idGPS);

        this.name = name;
        this.userID = userID;

        wifi = false;

        this.coordonnes = coordonnes;
        this.rayon = rayon;
    }

    public Position(String idWifi, String name, List<String> ssid, String userID) {
        super(idWifi);

        this.name = name;
        this.userID = userID;

        wifi = true;

        this.ssid = ssid;
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public boolean isWifi() {
        return wifi;
    }

    @Bindable
    public GeoPoint getCoordonnees() {
        return coordonnes;
    }

    public void setCoordonnees(GeoPoint coordonnes) {
        this.coordonnes = coordonnes;
        notifyPropertyChanged(BR.coordonnees);
    }

    @Bindable
    public int getRayon() {
        return rayon;
    }

    public void setRayon(int rayon) {
        this.rayon = rayon;
        notifyPropertyChanged(BR.rayon);
    }

    @Bindable
    public List<String> getSSID() {
        return ssid;
    }

    public void setSSID(List<String> ssid) {
        this.ssid = ssid;
        notifyPropertyChanged(BR.sSID);
    }

    public String getUserID() {
        return userID;
    }

    @android.support.annotation.NonNull
    @Override
    public String toString() {
        return this.getName();
    }
}
