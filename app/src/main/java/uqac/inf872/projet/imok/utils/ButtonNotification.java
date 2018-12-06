package uqac.inf872.projet.imok.utils;

import android.app.PendingIntent;

public class ButtonNotification {

    private final int logo;
    private final int nom;
    private final PendingIntent action;

    public ButtonNotification(int logo, int nom, PendingIntent action) {
        this.logo = logo;
        this.nom = nom;
        this.action = action;
    }

    public int getLogo() {
        return logo;
    }

    public int getNom() {
        return nom;
    }

    public PendingIntent getAction() {
        return action;
    }
}
