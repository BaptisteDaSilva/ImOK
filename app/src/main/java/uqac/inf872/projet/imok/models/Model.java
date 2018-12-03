package uqac.inf872.projet.imok.models;

import android.databinding.BaseObservable;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Model extends BaseObservable {
    @Exclude
    private String id;

    public Model() {
    }

    public Model(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
