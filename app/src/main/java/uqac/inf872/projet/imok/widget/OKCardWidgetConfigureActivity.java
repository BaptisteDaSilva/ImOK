package uqac.inf872.projet.imok.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uqac.inf872.projet.imok.R;
import uqac.inf872.projet.imok.api.OKCardHelper;
import uqac.inf872.projet.imok.controllers.activities.MainActivity;
import uqac.inf872.projet.imok.models.OKCard;
import uqac.inf872.projet.imok.utils.Utils;

import static android.os.SystemClock.sleep;

/**
 * The configuration screen for the {@link OKCardWidget OKCardWidget} AppWidget.
 */
public class OKCardWidgetConfigureActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "uqac.inf872.projet.imok.widget.OKCardWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";

    private static final Executor getOKCardExecutor = Executors.newSingleThreadExecutor();
    private static ArrayList<OKCard> listOKCard;
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @BindView(R.id.appwidget_list_ok_card)
    Spinner spinnerListOKCard;

    @BindView(R.id.appwidget_configuration)
    LinearLayout appwidgetConfiguration;

    @BindView(R.id.appwidget_configuration_connection)
    LinearLayout appwidgetConfigurationConnection;

    public OKCardWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveIdOKCardPref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadIdOKCardPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String idOKCard = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);

        if ( idOKCard != null ) {
            return idOKCard;
        } else {
            return "";
        }
    }

    static void deleteIdOKCardPref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.widget_ok_card_configure);

        ButterKnife.bind(this); //Configure Butterknife

        // Get the toolbar (Serialise)
        Toolbar toolbar = findViewById(R.id.toolbar);
        // Set the toolbar
        setSupportActionBar(toolbar);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        if ( Utils.getCurrentUser() == null ) {
            appwidgetConfiguration.setVisibility(View.GONE);
            appwidgetConfigurationConnection.setVisibility(View.VISIBLE);
        } else {

            appwidgetConfigurationConnection.setVisibility(View.GONE);
            appwidgetConfiguration.setVisibility(View.VISIBLE);

            // Find the widget id from the intent.
            Intent intent = getIntent();
            Bundle extras = intent.getExtras();
            if ( extras != null ) {
                mAppWidgetId = extras.getInt(
                        AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            }

            // If this activity was started with an intent without an app widget ID, finish with an error.
            if ( mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID ) {
                finish();
                return;
            }

            listOKCard = new ArrayList<>();

            Query query = OKCardHelper.getOKCard();

            if ( query != null ) {
                Task taskOKCard = query.get().addOnCompleteListener(getOKCardExecutor, task -> {
                    if ( task.isSuccessful() ) {

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            listOKCard.add(document.toObject(OKCard.class));
                        }
                    } else {
                        Utils.onFailureListener(this, task.getException());
                    }
                });

                while (!taskOKCard.isComplete()) {
                    sleep(50);
                }
            }

            ArrayAdapter<OKCard> adapter =
                    new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listOKCard);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinnerListOKCard.setAdapter(adapter);
        }
    }

    // -------------------
    // ACTIONS
    // -------------------

    @OnClick(R.id.add_button)
    public void onClickAddWidget(View view) {

        final Context context = OKCardWidgetConfigureActivity.this;

        // When the button is clicked, store the string locally
        OKCard OKCard = (OKCard) spinnerListOKCard.getSelectedItem();

        if ( OKCard != null ) {
            saveIdOKCardPref(context, mAppWidgetId, OKCard.getId());

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            OKCardWidget.getData(context, mAppWidgetId);
            OKCardWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
        }
        finish();
    }

    @OnClick(R.id.appwidget_btn_connection)
    public void onClickConnection(View view) {

        Intent connectionIntent = new Intent(getApplicationContext(), MainActivity.class);
        connectionIntent.putExtra(MainActivity.BUNDLE_KEY_CONNECTION_ASK, true);

        TaskStackBuilder.create(getApplicationContext())
                .addNextIntentWithParentStack(connectionIntent)
                .startActivities();
    }
}

