package uqac.inf872.projet.imok.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uqac.inf872.projet.imok.models.RecipientList;
import uqac.inf872.projet.imok.utils.Utils;

public class RecipientListHelper {

    private static final String COLLECTION_NAME = "recipient_list";

    // --- COLLECTION REFERENCE ---

    private static CollectionReference getRecipientListsCollection() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);

        return firestore.collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createRecipientList(String name, List<String> destinataires, String userID) {
        String id = RecipientListHelper.getRecipientListsCollection().document().getId();

        // Create Obj
        RecipientList recipientListToCreate = new RecipientList(id, name, destinataires, userID);

        return RecipientListHelper.getRecipientListsCollection().document(id).set(recipientListToCreate);
    }

    // --- GET ---

    public static Query getRecipientList() {
        FirebaseUser user = Utils.getCurrentUser();

        Query query = null;

        if ( user != null ) {
            query = RecipientListHelper.getRecipientListsCollection().whereEqualTo("userID", user.getUid()).orderBy("name");
        }

        return query;
    }

    public static Task<DocumentSnapshot> getRecipientList(String idRecipientList) {
        return RecipientListHelper.getRecipientListsCollection().document(idRecipientList).get();
    }

    // --- UPDATE ---

    public static Task<Void> updateRecipientList(RecipientList recipientList) {

        Map<String, Object> recpientListPropertie = new HashMap<>();

        recpientListPropertie.put("name", recipientList.getName());
        recpientListPropertie.put("recipients", recipientList.getRecipients());

        return RecipientListHelper.getRecipientListsCollection().document(recipientList.getIdList()).update(recpientListPropertie);
    }

    // --- DELETE ---

    public static Task<Void> deleteRecipientList(String idRecipientList) {
        return RecipientListHelper.getRecipientListsCollection().document(idRecipientList).delete();
    }

    public static Task<QuerySnapshot> getOKCardWithThisRecipientList(String id) {
        return OKCardHelper.getOKCard().whereEqualTo("idListe", id).get();
    }
}
