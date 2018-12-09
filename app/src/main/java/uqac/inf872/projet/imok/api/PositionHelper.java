package uqac.inf872.projet.imok.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uqac.inf872.projet.imok.models.Position;
import uqac.inf872.projet.imok.utils.Utils;

public class PositionHelper {

    private static final String COLLECTION_NAME = "position";

    // --- COLLECTION REFERENCE ---

    private static CollectionReference getPositionCollection() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);

        return firestore.collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static String createPosition() {
        return PositionHelper.getPositionCollection().document().getId();
    }

    public static Task<Void> createPositionGPS(String id, String name, double latitude, double longitude, int rayon, String userID) {
        // Create Obj
        Position positionToCreate = new Position(id, name, new GeoPoint(latitude, longitude), rayon, userID);

        return PositionHelper.getPositionCollection().document(id).set(positionToCreate);
    }

    public static Task<Void> createPositionWifi(String name, List<String> ssid, String userID) {
        String id = createPosition();

        // Create Obj
        Position positionToCreate = new Position(id, name, ssid, userID);

        return PositionHelper.getPositionCollection().document(id).set(positionToCreate);
    }

    // --- GET ---

    public static Query getPosition() {
        FirebaseUser user = Utils.getCurrentUser();

        Query query = null;

        if ( user != null ) {
            query = PositionHelper.getPositionCollection().whereEqualTo("userID", user.getUid()).orderBy("name");
        }

        return query;
    }

    public static Query getPositionGPS() {
        Query query = getPosition();

        if ( query != null ) {
            query = query.whereEqualTo("wifi", false);
        }

        return query;
    }

    public static Query getPositionWifi() {
        Query query = getPosition();

        if ( query != null ) {
            query = query.whereEqualTo("wifi", true);
        }

        return query;
    }

    public static Query getPositionWifiWithSSID(String ssid) {
        Query query = getPositionWifi();

        if ( query != null ) {
            query = query.whereArrayContains("ssid", ssid);
        }

        return query;
    }

    public static Task<DocumentSnapshot> getPosition(String id) {
        return PositionHelper.getPositionCollection().document(id).get();
    }

    // --- UPDATE ---

    public static Task<Void> updatePosition(Position position) {
        Map<String, Object> positionProperty = new HashMap<>();

        positionProperty.put("ssid", position.getSSID());
        positionProperty.put("coordonnees", position.getCoordonnees());
        positionProperty.put("rayon", position.getRayon());

        return PositionHelper.getPositionCollection().document(position.getId()).update(positionProperty);
    }

    // --- DELETE ---

    public static Task<Void> deletePosition(String id) {
        return PositionHelper.getPositionCollection().document(id).delete();
    }

    // UTILS

    public static Task<QuerySnapshot> getOKCardWithThisTrigger(String id) {
        return OKCardHelper.getOKCard().whereArrayContains("idTrigger", id).get();
    }
}
