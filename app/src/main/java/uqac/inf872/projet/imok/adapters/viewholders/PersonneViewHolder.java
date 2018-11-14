package uqac.inf872.projet.imok.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import uqac.inf872.projet.imok.R;
import uqac.inf872.projet.imok.adapters.PersonneAdapter;
import uqac.inf872.projet.imok.models.Personne;

public class PersonneViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @BindView(R.id.id)
    TextView textViewId;
    @BindView(R.id.nom)
    TextView textViewNom;
    @BindView(R.id.prenom)
    TextView textViewPrenom;

    // FOR DATA
    private WeakReference<PersonneAdapter.Listener> callbackWeakRef;

    public PersonneViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void updateWithItem(Personne personne, PersonneAdapter.Listener callback) {
        this.callbackWeakRef = new WeakReference<PersonneAdapter.Listener>(callback);
        this.textViewId.setText(String.valueOf(personne.getId()));
        this.textViewNom.setText(personne.getNom());
        this.textViewPrenom.setText(personne.getPrenom());
    }

    @Override
    public void onClick(View view) {
        PersonneAdapter.Listener callback = callbackWeakRef.get();
    }
}