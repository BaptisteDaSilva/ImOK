package uqac.inf872.projet.imok.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import uqac.inf872.projet.imok.R;
import uqac.inf872.projet.imok.models.OKCard;
import uqac.inf872.projet.imok.views.RobotoTextView;

public class OkCardViewHolder extends RecyclerView.ViewHolder {

    // FOR DESIGN

    @BindView(R.id.fragment_menu_ok_card_item_image)
    ImageView imageView;

    @BindView(R.id.fragment_menu_ok_card_item_name)
    RobotoTextView textView;

    public OkCardViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void updateWithOkCard(OKCard okCard, RequestManager glide) {
        glide.load(okCard.getUrlPicture())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);

        textView.setText(okCard.getName());
    }
}
