package uqac.inf872.projet.imok.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import uqac.inf872.projet.imok.R;
import uqac.inf872.projet.imok.models.RecipientList;

public class RecipientListViewHolder extends RecyclerView.ViewHolder {

    // FOR DESIGN
    @BindView(R.id.fragment_menu_recipient_list_item_image_list)
    ImageView imageView;

    @BindView(R.id.fragment_menu_recipient_list_item_name)
    TextView textView;

    @BindView(R.id.fragment_menu_recipient_list_item_list)
    TextView textViewList;

    public RecipientListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    // ---

    public void updateWithOkCard(RecipientList recipientList, RequestManager glide) {

        glide.asDrawable().load(R.drawable.ic_list_black).into(imageView);

        textView.setText(recipientList.getName());

        StringBuilder listRecipients = new StringBuilder();

        for (String recipient : recipientList.getRecipients()) {
            listRecipients.append(recipient).append("\n");
        }

        textViewList.setText(listRecipients);
    }
}
