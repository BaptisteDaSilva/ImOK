package uqac.inf872.projet.imok.models;

import android.databinding.Bindable;

import java.util.List;

import uqac.inf872.projet.imok.BR;

public class RecipientList extends Model {

    private String name;
    private List<String> recipients;
    private String userID;

    public RecipientList() {
    }

    public RecipientList(String idList, String name, List<String> recipients, String userID) {
        super(idList);

        this.name = name;
        this.recipients = recipients;
        this.userID = userID;
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
    public List<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
        notifyPropertyChanged(BR.recipients);
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
