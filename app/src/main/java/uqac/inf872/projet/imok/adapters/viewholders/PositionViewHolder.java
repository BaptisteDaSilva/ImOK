package uqac.inf872.projet.imok.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import uqac.inf872.projet.imok.R;
import uqac.inf872.projet.imok.models.Position;

public class PositionViewHolder extends RecyclerView.ViewHolder {

    // FOR DESIGN
    @BindView(R.id.fragment_menu_position_item_image)
    ImageView imageView;

    @BindView(R.id.fragment_menu_position_item_name)
    TextView textView;

    public PositionViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    // ---

    // TODO gerer
    public void updateWithPosition(Position position, RequestManager glide) {

        if ( position.isWifi() ) {
            glide.asDrawable().load(R.drawable.ic_location_white_large).into(imageView);
        } else {
            glide.asDrawable().load(R.drawable.ic_wifi_white_large).into(imageView);
        }

        textView.setText(position.getName());
    }
}
