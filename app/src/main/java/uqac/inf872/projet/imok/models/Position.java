package uqac.inf872.projet.imok.models;


import com.google.firebase.firestore.GeoPoint;

public class Position extends Model {

    private String name;
    private boolean wifi;
    private GeoPoint coordonnes;
    private int rayon;
    private String ssid;
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

    public Position(String idWifi, String name, String ssid, String userID) {
        super(idWifi);

        this.name = name;
        this.userID = userID;

        wifi = true;

        this.ssid = ssid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isWifi() {
        return wifi;
    }

    public GeoPoint getCoordonnees() {
        return coordonnes;
    }

    public void setCoordonnees(GeoPoint coordonnes) {
        this.coordonnes = coordonnes;
    }

    public int getRayon() {
        return rayon;
    }

    public void setRayon(int rayon) {
        this.rayon = rayon;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
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
