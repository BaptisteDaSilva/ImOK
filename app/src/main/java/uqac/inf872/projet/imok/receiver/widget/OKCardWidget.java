package uqac.inf872.projet.imok.receiver.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.RemoteViews;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;

import uqac.inf872.projet.imok.R;
import uqac.inf872.projet.imok.controllers.activities.OKCardActivity;
import uqac.inf872.projet.imok.models.OKCard;
import uqac.inf872.projet.imok.models.Position;
import uqac.inf872.projet.imok.models.RecipientList;

public class OKCardWidget extends AppWidgetProvider {

    // Intitulé de l'extra qui contient la direction du défilé
    private final static String EXTRA_DIRECTION = "extraDirection";

    // La valeur pour défiler vers la gauche
    private final static String EXTRA_PREVIOUS = "previous";

    // La valeur pour défiler vers la droite
    private final static String EXTRA_NEXT = "next";

    // Intitulé de l'extra qui contient l'indice actuel dans le tableau des tutos
    private final static String EXTRA_INDICE = "extraIndice";

    // Action qui indique qu'on essaie d'ouvrir un tuto sur internet
    private final static String ACTION_OPEN_OKCARD = "uqac.inf872.projet.imok.receiver.widget.action.ACTION_OPEN_OKCARD";

    private static ArrayList<OKCard> OK_CARDS = new ArrayList<>();

    private static HashMap<String, RecipientList> RECIPIENTS_LIST = new HashMap<>();

    private static HashMap<String, Position> POSITION = new HashMap<>();

    // Indice actuel dans le tableau des tutos
    private int indice = 0;

    public static void addOKCard(OKCard okCard) {
        OK_CARDS.add(okCard);
    }

    public static void deleteOKCard(OKCard okCard) {
        OK_CARDS.remove(okCard);
    }

    private static void getData() {
//        Query query = OKCardHelper.getOKCard();
//
//        if (query != null) {
//            query.get().addOnCompleteListener(command -> {
//                if ( !command.getResult().isEmpty() ) {
//
//                    for(QueryDocumentSnapshot queryDoc : command.getResult()) {
//                        OK_CARDS.add(queryDoc.toObject(OKCard.class));
//                    }
//                }
//            });
//        }

        ArrayList<String> positionMaison = new ArrayList<>();
        positionMaison.add("0");
        positionMaison.add("1");

        ArrayList<String> positionBoulot = new ArrayList<>();
        positionBoulot.add("2");

        OK_CARDS.add(new OKCard("6uwdS64ibhsY5hKcnDTz", "Maison", "Je suis arrivé à la maison", "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wCEAAkGBxIPEhUQEBIVFRUVFxcVFhcVFRYQGBEVFRUXFhYVFhcYHSggGBslGxcVITEhJykrLi4uFyAzODM4NyktLisBCgoKDg0OGxAQGi8mICYtLy0tLS0tLS0tMi0tLS4tLS0tLS0tLS0tLS8tLS0tLTAtLS0vLS0tLS0tLS0tLS0tLf/AABEIAOUA3AMBEQACEQEDEQH/xAAcAAEAAQUBAQAAAAAAAAAAAAAABQIDBAYHAQj/xABIEAABAgMDBQkLCwUAAwAAAAABAAIDBBEFIUEGEjFRYQcTIjJScZGSsRUWJFNicnOBobLRFDRCg6LBw9Lh8PEjM0OzwmOCk//EABsBAQACAwEBAAAAAAAAAAAAAAABBAIDBQYH/8QAMxEBAAIBAgQDBgcAAgMBAAAAAAECAwQRBRIhMUFRcRNhgbHR8BQiMpGhweEj8UJDYjP/2gAMAwEAAhEDEQA/AO4oCAgICAgICAgICAgII+3LZgSMIx5l4YwXDEvdg1o0uOzYToCxtaKxvLdgwXzW5KRvLIkJ2HMQ2xoLw9jxVrmmoI+47MFMTExvDC9LY7TW0bTDIUsBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEGvZYZXQLLh50U50Rw/pwgaOiHWeS3W7tNy15MkUjquaTRZNTbavbxlwu2LUmrXjGNHdwW1DQOJCHJYMToqdJxwXPyZJtO8vX6TR0w15afGfNmZH5Vx7HjZpq+A81fDrccN8h10P7dBwIyxZZpPuaNfw+uor16WjtP34fJ3yx7VgzkJseXeHsdoI0g4tcMCNS6NbRaN4ePy4b4rzS8bSzVLWICAgICAgICAgICAgICAgICAgICAgINHy93QoVnAwINIkzTi6Wwa6DEIx8nTrpdXRlzRTpHd1NBw2+o/PbpX5+n1cdhy8aeiGZmnucXGpc48J+wclvNdS4Kha0zO8vWYsNcdYrWNoTRhhrc1ooALgFjKxXvCOmIDYgzXD4g6woZ2iJ7mTWUUzY8bPh8KG7jwyaNige64YHDaNO7Hlmk7w5mt0NM9eW3fwn7+TvuTtvQLQgiPLuqDc5pudDdi14wPbpFy6NLxaN4eO1GnvgvyXj/AFKLJoEBAQEBAQEBAQEBAQEBAQEBAQEBByrdB3TczOlbOcC7ivji8NwzYWt3laBhrFXLn26Vd7h/Cub/AJM0dPCPr9HP7MsYk77MVJJrmm8km+r9Z2dKpTL0laxCcojNRFFx5iollXujli2qIsIPGa4VBUomInpKxY1rTFkxxGgO4Juc08WK3kvGvUehbceSazvDn6vR0zU5b/CXfMk8p4Fpwt9gmjhQRIZPChOOB1g30djz1A6NMkXjeHjtVpMmnvy2+E+abWaqICAgICAgICAgICAgICAgICC1NTLILHRIrgxjRVznGgaBiSomdussq1m08tY3lxLLvdCi2gTKSQc2CeCTofMDGvJZsxGnUqWXPzdI7PUaDhcYdsmTrb5f79+9EWRYrYPDfRz/AGM5tu1V3YShChKkhErcUXHmPYollXvCNWLc8RCl7A4UIqCpRPVhyM3Hs2M2ZlnkEXawQdLHjFp+GNFspeazvCnqdNTLSaXjeHeMissINqQqs4EVo/qQib2+U3lN29K6OPJF4eO1mivprbT1jwn78WyLYpCAgICAgICAgICAgICAgII63bbgSMIx5h4a0XAaXPdg1gxP70LG14rG8t2DBkz35KRvLhWVGVE1bUXe2AsgNNWw68EeXFP0ndmGJNDJlm8+56zRaDHpq+dvGfoyrMstku2jb3HjOOk7BqGxal7dlkIlSQiVJChLDmI+DfWfgsZlvpTxliOChnMKUYilClwrcdCCPY6LJRWzEs9zHNNQRpbsOtp0UPMVnW0xO8KufBW9ZraN4l3HIHLmFabMx9Icw0cNmDwPpw66Rs0joJ6GLLF497yGv4fbTW3jrXwn+pbetznCAgICAgICAgICAgICDXMsssIFlw86Ic+K4f04QNHP2nktrj0VWvJkikdVzR6LJqbbV6R4z9+LiU5MzVsxt/mHUYLhS5kMciG3Xt6SqF7zed5es0+nx6enLSPqnpSTZBbmQxQe0nWTiVi3brpCCkhQyUONLyiY6o+Ymc64aO1YTK1THt1liveGipNAMSobJnbrKoGt4QUuajGYUFSweICIR0WC+XeI8u4tc05wLTQsIxGxZ1ttLRlxVtWYmN4dm3PN0BloAS8wQyZA5mxwNLmana2+sXVpfxZot0nu8lxDhtsH56da/L7829re5QgICAgICAgICAgINEy/3Q4dn1gQKRJml40sgbX63eT00uroy5or0ju6ug4ZbP8Anv0r/M+n1cpkbLizsQzU49zs85xLjwov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", "0", positionMaison, "0jRbKkRNsoPKHRks5SXCwOfxvuE3"));
        OK_CARDS.add(new OKCard("1", "Travail", "Je suis arrivé au boulot", "", "0", positionBoulot, "0jRbKkRNsoPKHRks5SXCwOfxvuE3"));

        ArrayList<String> personnes = new ArrayList<>();
        personnes.add("Baptiste RIBEIRO DA SILVA");
        RECIPIENTS_LIST.put("0", new RecipientList("0", "Moi", personnes, "0jRbKkRNsoPKHRks5SXCwOfxvuE3"));

        POSITION.put("0", new Position("0", "Maison", "SSID", "0jRbKkRNsoPKHRks5SXCwOfxvuE3"));
        POSITION.put("1", new Position("1", "Maison", new GeoPoint(0, 0), 30, "0jRbKkRNsoPKHRks5SXCwOfxvuE3"));
        POSITION.put("2", new Position("2", "Boulot", new GeoPoint(0, 0), 30, "0jRbKkRNsoPKHRks5SXCwOfxvuE3"));
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);

        OK_CARDS = new ArrayList<>();
        RECIPIENTS_LIST = new HashMap<>();
        POSITION = new HashMap<>();
    }

    @Override
    public void onEnabled(Context context) {
        getData();

        super.onEnabled(context);
    }

    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        OK_CARDS = new ArrayList<>();
        RECIPIENTS_LIST = new HashMap<>();
        POSITION = new HashMap<>();

        getData();

        super.onRestored(context, oldWidgetIds, newWidgetIds);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        for (int appWidgetId : appWidgetIds) {
            // On récupère le RemoteViews qui correspond à l'AppWidget
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

            if ( OK_CARDS.isEmpty() ) {

                // TODO gérer le cas ou il n'y a plus d'OKCard

            } else {

                // On met les bon texte
                views.setTextViewText(R.id.widget_ok_card_name, OK_CARDS.get(indice).getName());
                views.setImageViewUri(R.id.widget_ok_card_image, Uri.parse(OK_CARDS.get(indice).getUrlPicture()));
                views.setTextViewText(R.id.widget_ok_card_message, OK_CARDS.get(indice).getMessage());

//                RecipientListHelper.getRecipientList(OK_CARDS.get(indice).getIdListe())
////                        .addOnSuccessListener(documentSnapshot ->
////                        {
////                            RecipientList currentRecipientList = documentSnapshot.toObject(RecipientList.class);
////
////                            views.setTextViewText(R.id.widget_ok_card_recipient_list_name, currentRecipientList.getName());
////
////                            StringBuilder sb = new StringBuilder();
////
////                            for (String s : currentRecipientList.getRecipients()) {
////                                sb.append("- ").append(s).append("\n");
////                            }
////
////                            views.setTextViewText(R.id.widget_ok_card_recipient_list, sb.toString());
////                        });

                RecipientList currentRecipientList = RECIPIENTS_LIST.get(OK_CARDS.get(indice).getIdListe());

                views.setTextViewText(R.id.widget_ok_card_recipient_list_name, currentRecipientList.getName());

                StringBuilder sb = new StringBuilder();

                for (String s : currentRecipientList.getRecipients()) {
                    sb.append("- ").append(s).append("\n");
                }

                views.setTextViewText(R.id.widget_ok_card_recipient_list, sb.toString());

//                for (String idPosition : OK_CARDS.get(indice).getIdTrigger()) {
//                    PositionHelper.getPosition(idPosition)
//                            .addOnSuccessListener(documentSnapshotRecipient ->
//                            {
//                                Position position = documentSnapshotRecipient.toObject(Position.class);
//
//                                if ( position.isWifi() ) {
//                                    views.setViewVisibility(R.id.widget_ok_card_wifi, View.VISIBLE);
//                                    views.setTextViewText(R.id.widget_ok_card_wifi_name, position.getName());
//                                } else {
//                                    views.setViewVisibility(R.id.widget_ok_card_gps, View.VISIBLE);
//                                    views.setTextViewText(R.id.widget_ok_card_gps_name, position.getName());
//                                }
//                            });
//                }


                views.setViewVisibility(R.id.widget_ok_card_wifi, View.GONE);
                views.setViewVisibility(R.id.widget_ok_card_gps, View.GONE);

                for (String idPosition : OK_CARDS.get(indice).getIdTrigger()) {
                    Position position = POSITION.get(idPosition);

                    if ( position.isWifi() ) {
                        views.setViewVisibility(R.id.widget_ok_card_wifi, View.VISIBLE);
                        views.setTextViewText(R.id.widget_ok_card_wifi_name, position.getName());
                    } else {
                        views.setViewVisibility(R.id.widget_ok_card_gps, View.VISIBLE);
                        views.setTextViewText(R.id.widget_ok_card_gps_name, position.getName());
                    }
                }

                // La section suivante est destinée à l'ouverture d'une OKCard
                // L'intent ouvre cette classe même…
                Intent linkIntent = new Intent(context, OKCardWidget.class);

                // Action l'action ACTION_OPEN_OKCARD
                linkIntent.setAction(ACTION_OPEN_OKCARD);
                // Et l'id de l'OKCard a ouvrir
                linkIntent.putExtra(OKCardActivity.BUNDLE_KEY_OK_CARD_ID, OK_CARDS.get(indice).getIdCard());
                linkIntent.putExtra(OKCardActivity.BUNDLE_KEY_OK_CARD_IMAGE_URL, OK_CARDS.get(indice).getUrlPicture());

                // On ajoute l'intent dans un PendingIntent
                PendingIntent linkPending = PendingIntent.getBroadcast(context, 2, linkIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                views.setOnClickPendingIntent(R.id.widget_ok_card_name, linkPending);
                views.setOnClickPendingIntent(R.id.widget_ok_card_image, linkPending);
            }

            // La prochaine section est destinée au bouton qui permet de passer a la carte suivante
            //********************************************************
            //*******************NEXT*********************************
            //********************************************************
            Intent nextIntent = new Intent(context, OKCardWidget.class);

            // On veut que l'intent lance la mise à jour
            nextIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

            // On n'oublie pas les identifiants
            nextIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

            // On rajoute la direction
            nextIntent.putExtra(EXTRA_DIRECTION, EXTRA_NEXT);

            // Ainsi que l'indice
            nextIntent.putExtra(EXTRA_INDICE, indice);

            // Les données inutiles mais qu'il faut rajouter
            Uri data = Uri.withAppendedPath(Uri.parse("WIDGET://widget/id/"), String.valueOf(R.id.widget_btn_right));
            nextIntent.setData(data);

            // On insère l'intent dans un PendingIntent
            PendingIntent nextPending = PendingIntent.getBroadcast(context, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Et on l'associe à l'activation du bouton
            views.setOnClickPendingIntent(R.id.widget_btn_right, nextPending);

            // La prochaine section est destinée au bouton qui permet de passer a la carte précédente
            //********************************************************
            //*******************PREVIOUS*****************************
            //********************************************************

            Intent previousIntent = new Intent(context, OKCardWidget.class);

            previousIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            previousIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            previousIntent.putExtra(EXTRA_DIRECTION, EXTRA_PREVIOUS);
            previousIntent.putExtra(EXTRA_INDICE, indice);

            data = Uri.withAppendedPath(Uri.parse("WIDGET://widget/id/"), String.valueOf(R.id.widget_btn_left));
            previousIntent.setData(data);

            PendingIntent previousPending = PendingIntent.getBroadcast(context, 1, previousIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            views.setOnClickPendingIntent(R.id.widget_btn_left, previousPending);

            // Et il faut mettre à jour toutes les vues
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Si l'action est celle d'ouverture de carte
        if ( ACTION_OPEN_OKCARD.equals(intent.getAction()) ) {
            Intent intentOKCardActivity = new Intent(context.getApplicationContext(), OKCardActivity.class);
            intentOKCardActivity.putExtras(intent.getExtras());
            context.startActivity(intentOKCardActivity);
        } else {
            // Sinon, s'il s'agit d'une demande de mise à jour
            // On récupère l'indice passé en extra, ou -1 s'il n'y a pas d'indice
            int tmp = intent.getIntExtra(EXTRA_INDICE, -1);

            // S'il y avait bien un indice passé
            if ( tmp != -1 ) {
                // On récupère la direction
                String extra = intent.getStringExtra(EXTRA_DIRECTION);
                // Et on calcule l'indice voulu par l'utilisateur
                if ( extra.equals(EXTRA_PREVIOUS) ) {
                    indice = (tmp - 1) % OK_CARDS.size();
                    if ( indice < 0 )
                        indice += OK_CARDS.size();
                } else if ( extra.equals(EXTRA_NEXT) )
                    indice = (tmp + 1) % OK_CARDS.size();
            }
        }

        // On revient au traitement naturel du Receiver, qui va lancer onUpdate s'il y a demande de mise à jour
        super.onReceive(context, intent);
    }
}
