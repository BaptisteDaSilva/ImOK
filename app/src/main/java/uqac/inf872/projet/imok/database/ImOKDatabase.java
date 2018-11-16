package uqac.inf872.projet.imok.database;

import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

//@Database(entities = {Personne.class}, version = 1, exportSchema = false)
public abstract class ImOKDatabase extends RoomDatabase {

    // --- SINGLETON ---
    private static volatile ImOKDatabase INSTANCE;

    // --- INSTANCE ---
    public static ImOKDatabase getInstance(Context context) {
        if ( INSTANCE == null ) {
            synchronized (ImOKDatabase.class) {
                if ( INSTANCE == null ) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ImOKDatabase.class, "ImOKDatabase.db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // ---

    // --- DAO ---
//    public abstract PersonneDAO personneDao();
}
