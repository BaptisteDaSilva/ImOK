package uqac.inf872.projet.imok.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;

import uqac.inf872.projet.imok.models.Position;

public class PositionHelper {

    private static final String COLLECTION_NAME = "position";

    // --- COLLECTION REFERENCE ---

    private static CollectionReference getPositionCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createPositionGPS(String name, double longitude, double latitude, int rayon, String userID) {
        String id = PositionHelper.getPositionCollection().document().getId();

        // Create Obj
        Position positionToCreate = new Position(id, name, new GeoPoint(latitude, longitude), rayon, userID);

        return PositionHelper.getPositionCollection().document(id).set(positionToCreate);
    }

    public static Task<Void> createPositionWifi(String name, String ssid, String userID) {
        String id = PositionHelper.getPositionCollection().document().getId();

        // Create Obj
        Position positionToCreate = new Position(id, name, ssid, userID);

        return PositionHelper.getPositionCollection().document().set(positionToCreate);
    }

    // --- GET ---

    public static Query getPosition() {
        return PositionHelper.getPositionCollection().orderBy("name");
    }

    public static Task<DocumentSnapshot> getPosition(String id) {
        return PositionHelper.getPositionCollection().document(id).get();
    }

    // --- UPDATE ---

    public static Task<Void> updatePositionName(String id, String name) {
        return PositionHelper.getPositionCollection().document(id).update("name", name);
    }

    // --- DELETE ---

    public static Task<Void> deletePosition(String id) {
        return PositionHelper.getPositionCollection().document(id).delete();
    }
}
