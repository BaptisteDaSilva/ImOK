package uqac.inf872.projet.imok.models;

import java.util.List;

public class OKCard extends Model {

    private String name;
    private String message;
    private String urlPicture;
    private String idListe;
    private List<String> idTrigger;
    private String userID;

    public OKCard() {
        super();
    }

    public OKCard(String idCard, String name, String message, String urlPicture, String idListe, List<String> idTrigger, String userID) {
        super(idCard);

        this.name = name;
        this.message = message;
        this.urlPicture = urlPicture;
        this.idListe = idListe;
        this.idTrigger = idTrigger;
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(String urlPicture) {
        this.urlPicture = urlPicture;
    }

    public String getIdListe() {
        return idListe;
    }

    public void setIdListe(String idListe) {
        this.idListe = idListe;
    }

    public List<String> getIdTrigger() {
        return idTrigger;
    }

    public void setIdTrigger(List<String> idTrigger) {
        this.idTrigger = idTrigger;
    }

    public String getUserID() {
        return userID;
    }

    @android.support.annotation.NonNull
    @Override
    public String toString() {
        return this.getName();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof OKCard && ((OKCard) obj).getId().equals(this.getId());

    }
}
