package uqac.inf872.projet.imok.models;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Model {
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
