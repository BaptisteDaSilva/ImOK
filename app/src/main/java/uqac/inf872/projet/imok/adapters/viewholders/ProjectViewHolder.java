package uqac.inf872.projet.imok.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import uqac.inf872.projet.imok.R;
import uqac.inf872.projet.imok.models.Project;

/**
 * Created by Philippe on 27/03/2018.
 */

public class ProjectViewHolder extends RecyclerView.ViewHolder {

    // FOR DESIGN
    @BindView(R.id.fragment_menu_item_image)
    ImageView imageView;

    public ProjectViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    // ---

    public void updateWithProject(Project project, RequestManager glide) {
        glide.load(project.getCovers().getOriginal()).transition(DrawableTransitionOptions.withCrossFade()).into(imageView);
    }
}
