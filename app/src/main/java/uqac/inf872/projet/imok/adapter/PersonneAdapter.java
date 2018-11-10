package uqac.inf872.projet.imok.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import uqac.inf872.projet.imok.R;
import uqac.inf872.projet.imok.models.Personne;
import uqac.inf872.projet.imok.view.PersonneViewHolder;

public class PersonneAdapter extends RecyclerView.Adapter<PersonneViewHolder> {

    private final Listener callback;
    // FOR DATA
    private List<Personne> personnes;

    // CONSTRUCTOR
    public PersonneAdapter(Listener callback) {
        this.personnes = new ArrayList<>();
        this.callback = callback;
    }

    @Override
    public PersonneViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_personne, parent, false);

        return new PersonneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PersonneViewHolder viewHolder, int position) {
        viewHolder.updateWithItem(this.personnes.get(position), this.callback);
    }

    @Override
    public int getItemCount() {
        return this.personnes.size();
    }

    public Personne getPersonne(int position) {
        return this.personnes.get(position);
    }

    public void updateData(List<Personne> personnes) {
        this.personnes = personnes;
        this.notifyDataSetChanged();
    }

    // CALLBACK
    public interface Listener {
    }
}
