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
import uqac.inf872.projet.imok.adapters.viewholders.PositionViewHolder;
import uqac.inf872.projet.imok.models.Position;

public class PositionAdapter extends FirestoreRecyclerAdapter<Position, PositionViewHolder> {

    // FOR DATA
    private RequestManager glide;

    public PositionAdapter(@NonNull FirestoreRecyclerOptions<Position> options, RequestManager glide) {
        super(options);
        this.glide = glide;
    }

    @NonNull
    @Override
    public PositionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_menu_position_item, parent, false);

        return new PositionViewHolder(view);
    }


    @Override
    protected void onBindViewHolder(@NonNull PositionViewHolder holder, int position, @NonNull Position model) {
        holder.updateWithPosition(model, this.glide);
    }
}
