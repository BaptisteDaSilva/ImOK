package uqac.inf872.projet.imok.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uqac.inf872.projet.imok.models.OKCard;

/**
 * Created by Philippe on 30/01/2018.
 */

public class OKCardHelper {

    private static final String COLLECTION_NAME = "OKCard";

    // --- COLLECTION REFERENCE ---

    private static CollectionReference getOKCardsCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
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
        return OKCardHelper.getOKCardsCollection().orderBy("name");
    }

    public static Task<DocumentSnapshot> getOKCard(String idCard) {
        return OKCardHelper.getOKCardsCollection().document(idCard).get();
    }

    // --- UPDATE ---

    public static Task<Void> updateOKCard(OKCard okCard) {

        Map<String, Object> okCardPropertie = new HashMap<>();

        okCardPropertie.put("name", okCard.getName());
        okCardPropertie.put("message", okCard.getMessage());
        okCardPropertie.put("urlPicture", okCard.getUrlPicture());
        okCardPropertie.put("idListe", okCard.getIdListe());
        okCardPropertie.put("idTrigger", okCard.getIdTrigger());

        return OKCardHelper.getOKCardsCollection().document(okCard.getIdCard()).update(okCardPropertie);
    }

    // --- DELETE ---

    public static Task<Void> deleteOKCard(String idCard) {
        return OKCardHelper.getOKCardsCollection().document(idCard).delete();
    }
}
