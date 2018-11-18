package uqac.inf872.projet.imok.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import uqac.inf872.projet.imok.R;
import uqac.inf872.projet.imok.adapters.viewholders.OkCardViewHolder;
import uqac.inf872.projet.imok.models.OKCard;

public class OKCardAdapter extends FirestoreRecyclerAdapter<OKCard, OkCardViewHolder> {

    // FOR DATA
    private RequestManager glide;

    public OKCardAdapter(@NonNull FirestoreRecyclerOptions<OKCard> options, RequestManager glide) {
        super(options);
        this.glide = glide;
    }

    @NonNull
    @Override
    public OkCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_menu_ok_card_item, parent, false);

        return new OkCardViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull OkCardViewHolder holder, int position, @NonNull OKCard model) {
        holder.updateWithOkCard(model, this.glide);
    }
}
