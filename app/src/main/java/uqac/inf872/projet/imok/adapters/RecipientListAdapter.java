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
import uqac.inf872.projet.imok.adapters.viewholders.RecipientListViewHolder;
import uqac.inf872.projet.imok.models.RecipientList;

public class RecipientListAdapter extends FirestoreRecyclerAdapter<RecipientList, RecipientListViewHolder> {

    // FOR DATA
    private RequestManager glide;

    public RecipientListAdapter(@NonNull FirestoreRecyclerOptions<RecipientList> options, RequestManager glide) {
        super(options);
        this.glide = glide;
    }

    @NonNull
    @Override
    public RecipientListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_menu_recipient_list_item, parent, false);

        return new RecipientListViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull RecipientListViewHolder holder, int position, @NonNull RecipientList model) {
        holder.updateWithOkCard(model, this.glide);
    }
}
