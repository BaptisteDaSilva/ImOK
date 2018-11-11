package uqac.inf872.projet.imok.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import uqac.inf872.projet.imok.R;

public class TutoWidget extends AppWidgetProvider {
    // Les tutos que propose notre widget
    private final static Tuto TUTO_ARRAY[] = {
            new Tuto("Développez votre première application Android", "https://openclassrooms.com/fr/courses/4517166-developpez-votre-premiere-application-android"),
            new Tuto("Développez des applications robustes et fiables", "https://openclassrooms.com/fr/courses/4568526-developpez-des-applications-robustes-et-fiables"),
            new Tuto("Construisez une interface utilisateur flexible et adaptative", "https://openclassrooms.com/fr/courses/4568596-construisez-une-interface-utilisateur-flexible-et-adaptative"),
            new Tuto("Récupérez et affichez des données distantes", "https://openclassrooms.com/fr/courses/4568576-recuperez-et-affichez-des-donnees-distantesl"),
            new Tuto("Créez un backend scalable et performant sur Firebase", "https://openclassrooms.com/fr/courses/4872916-creez-un-backend-scalable-et-performant-sur-firebase"),
            new Tuto("Gérez vos données localement pour avoir une application 100 % hors-ligne", "https://openclassrooms.com/fr/courses/4568746-gerez-vos-donnees-localement-pour-avoir-une-application-100-hors-ligne"),
            new Tuto("Personnalisez vos applications", "https://openclassrooms.com/fr/courses/4568621-personnalisez-vos-applications"),
            new Tuto("Initiez-vous à Kotlin", "https://openclassrooms.com/fr/courses/5353106-initiez-vous-a-kotlin")
    };

    // Intitulé de l'extra qui contient la direction du défilé
    private final static String EXTRA_DIRECTION = "extraDirection";

    // La valeur pour défiler vers la gauche
    private final static String EXTRA_PREVIOUS = "previous";

    // La valeur pour défiler vers la droite
    private final static String EXTRA_NEXT = "next";

    // Intitulé de l'extra qui contient l'indice actuel dans le tableau des tutos
    private final static String EXTRA_INDICE = "extraIndice";

    // Action qui indique qu'on essaie d'ouvrir un tuto sur internet
    private final static String ACTION_OPEN_TUTO = "uqac.inf872.projet.imok.widget.action.OPEN_TUTO";

    // Indice actuel dans le tableau des tutos
    private int indice = 0;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        for (int appWidgetId : appWidgetIds) {
            // On récupère le RemoteViews qui correspond à l'AppWidget
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

            // On met le bon texte dans le bouton
            views.setTextViewText(R.id.link, TUTO_ARRAY[indice].getIntitule());

            // La prochaine section est destinée au bouton qui permet de passer au tuto suivant
            //********************************************************
            //*******************NEXT*********************************
            //********************************************************
            Intent nextIntent = new Intent(context, TutoWidget.class);

            // On veut que l'intent lance la mise à jour
            nextIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

            // On n'oublie pas les identifiants
            nextIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

            // On rajoute la direction
            nextIntent.putExtra(EXTRA_DIRECTION, EXTRA_NEXT);

            // Ainsi que l'indice
            nextIntent.putExtra(EXTRA_INDICE, indice);

            // Les données inutiles mais qu'il faut rajouter
            Uri data = Uri.withAppendedPath(Uri.parse("WIDGET://widget/id/"), String.valueOf(R.id.next));
            nextIntent.setData(data);

            // On insère l'intent dans un PendingIntent
            PendingIntent nextPending = PendingIntent.getBroadcast(context, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Et on l'associe à l'activation du bouton
            views.setOnClickPendingIntent(R.id.next, nextPending);

            // La prochaine section est destinée au bouton qui permet de passer au tuto précédent
            //********************************************************
            //*******************PREVIOUS*****************************
            //********************************************************

            Intent previousIntent = new Intent(context, TutoWidget.class);

            previousIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            previousIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            previousIntent.putExtra(EXTRA_DIRECTION, EXTRA_PREVIOUS);
            previousIntent.putExtra(EXTRA_INDICE, indice);

            data = Uri.withAppendedPath(Uri.parse("WIDGET://widget/id/"), String.valueOf(R.id.previous));
            previousIntent.setData(data);

            PendingIntent previousPending = PendingIntent.getBroadcast(context, 1, previousIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            views.setOnClickPendingIntent(R.id.previous, previousPending);


            // La section suivante est destinée à l'ouverture d'un tuto dans le navigateur
            //********************************************************
            //*******************LINK*********************************
            //********************************************************
            // L'intent ouvre cette classe même…
            Intent linkIntent = new Intent(context, TutoWidget.class);

            // Action l'action ACTION_OPEN_TUTO
            linkIntent.setAction(ACTION_OPEN_TUTO);
            // Et l'adresse du site à visiter
            linkIntent.setData(TUTO_ARRAY[indice].getAdresse());

            // On ajoute l'intent dans un PendingIntent
            PendingIntent linkPending = PendingIntent.getBroadcast(context, 2, linkIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.link, linkPending);

            // Et il faut mettre à jour toutes les vues
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Si l'action est celle d'ouverture du tutoriel
        if (ACTION_OPEN_TUTO.equals(intent.getAction())) {
            Intent link = new Intent(Intent.ACTION_VIEW);
            link.setData(intent.getData());
            link.addCategory(Intent.CATEGORY_DEFAULT);
            // Comme on ne se trouve pas dans une activité, on demande à créer une nouvelle tâche
            link.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(link);
        } else {
            // Sinon, s'il s'agit d'une demande de mise à jour
            // On récupère l'indice passé en extra, ou -1 s'il n'y a pas d'indice
            int tmp = intent.getIntExtra(EXTRA_INDICE, -1);

            // S'il y avait bien un indice passé
            if (tmp != -1) {
                // On récupère la direction
                String extra = intent.getStringExtra(EXTRA_DIRECTION);
                // Et on calcule l'indice voulu par l'utilisateur
                if (extra.equals(EXTRA_PREVIOUS)) {
                    indice = (tmp - 1) % TUTO_ARRAY.length;
                    if (indice < 0)
                        indice += TUTO_ARRAY.length;
                } else if (extra.equals(EXTRA_NEXT))
                    indice = (tmp + 1) % TUTO_ARRAY.length;
            }
        }

        // On revient au traitement naturel du Receiver, qui va lancer onUpdate s'il y a demande de mise à jour
        super.onReceive(context, intent);
    }

}
