package uqac.inf872.projet.imok.models;

import java.util.List;

public class RecipientList {

    private String idList;
    private String name;
    private List<String> recipients;
    private String userID;

    public RecipientList() {
    }

    public RecipientList(String idList, String name, List<String> recipients, String userID) {
        this.idList = idList;
        this.name = name;
        this.recipients = recipients;
        this.userID = userID;
    }

    public String getIdList() {
        return idList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
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
