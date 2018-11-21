package uqac.inf872.projet.imok.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Utils {

    public static final String TAG = "I'm OK";

    // --------------------
    // ERROR HANDLER
    // --------------------

    public static OnFailureListener onFailureListener(Context context) {
        return e -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
    }

    public static void onFailureListener(Context context, Exception e) {
        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
    }

    // --------------------
    // UTILS
    // --------------------

    @Nullable
    public static FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static Boolean isCurrentUserLogged() {
        return (getCurrentUser() != null);
    }
}
