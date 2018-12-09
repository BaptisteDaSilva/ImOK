package uqac.inf872.projet.imok.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uqac.inf872.projet.imok.models.OKCard;
import uqac.inf872.projet.imok.utils.Utils;

public class OKCardHelper {

    private static final String COLLECTION_NAME = "OKCard";

    // --- COLLECTION REFERENCE ---

    private static CollectionReference getOKCardsCollection() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);

        return firestore.collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createOKCard(String name, String message, String urlPicture, String idListe, List<String> idTrigger, String userID) {
        String id = OKCardHelper.getOKCardsCollection().document().getId();

        // 1 - Create Obj
        OKCard OKCardToCreate = new OKCard(id, name, message, urlPicture, idListe, idTrigger, userID);

        return OKCardHelper.getOKCardsCollection().document(id).set(OKCardToCreate);
    }

    // --- GET ---

    public static Query getOKCard() {
        FirebaseUser user = Utils.getCurrentUser();

        Query query = null;

        if ( user != null ) {
            query = OKCardHelper.getOKCardsCollection().whereEqualTo("userID", user.getUid()).orderBy("name");
        }

        return query;
    }

    public static DocumentReference getOKCard(String idCard) {
        return OKCardHelper.getOKCardsCollection().document(idCard);
    }

    public static Query getOKCardPosition(String idPosition) {
        Query query = getOKCard();

        if ( query != null ) {
            query = query.whereArrayContains("idTrigger", idPosition);
        }

        return query;
    }

    // --- UPDATE ---

    public static Task<Void> updateOKCard(OKCard okCard) {

        Map<String, Object> okCardProperty = new HashMap<>();

        okCardProperty.put("name", okCard.getName());
        okCardProperty.put("message", okCard.getMessage());
        okCardProperty.put("urlPicture", okCard.getUrlPicture());
        okCardProperty.put("idListe", okCard.getIdListe());
        okCardProperty.put("idTrigger", okCard.getIdTrigger());

        return OKCardHelper.getOKCardsCollection().document(okCard.getId()).update(okCardProperty);
    }

    // --- DELETE ---

    public static Task<Void> deleteOKCard(String idCard) {
        return OKCardHelper.getOKCardsCollection().document(idCard).delete();
    }
}
