package uqac.inf872.projet.imok;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.perf.metrics.AddTrace;

import java.util.ArrayList;
import java.util.List;

import uqac.inf872.projet.imok.adapter.PersonneAdapter;
import uqac.inf872.projet.imok.injections.Injection;
import uqac.inf872.projet.imok.injections.ViewModelFactory;
import uqac.inf872.projet.imok.models.Personne;
import uqac.inf872.projet.imok.util.ItemClickSupport;
import uqac.inf872.projet.imok.view.PersonneViewModel;

public class BDActivity extends BaseActivity implements PersonneAdapter.Listener {

    // FOR DESIGN
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    // FOR DATA
    private PersonneViewModel personneViewModel;
    private PersonneAdapter adapter;

    private List<Personne> personnes;

    @Override
    @AddTrace(name = "test_trace")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bd);

        recyclerView = findViewById(R.id.fragment_main_recycler_view);
        swipeRefreshLayout = findViewById(R.id.activity_bd__swipe_container);

        // 8 - Configure RecyclerView & ViewModel
        this.configureRecyclerView();
        this.configureOnClickRecyclerView();
        this.configureSwipeRefreshLayout();
        this.configureViewModel();

        // 9 - Get personnes from Database
        this.getPersonnes();
    }

    // -------------------
    // DATA
    // -------------------

    // 2 - Configuring ViewModel
    private void configureViewModel() {
        ViewModelFactory mViewModelFactory = Injection.provideViewModelFactory(this);
        this.personneViewModel = ViewModelProviders.of(this, mViewModelFactory).get(PersonneViewModel.class);
    }

    // 3 - Get all personnes
    private void getPersonnes() {
        this.personneViewModel.getPersonnes().observe(this, this::updateItemsList);
    }

    // 3 - Create a new personne
    private void createPersonne() {
        Personne item = new Personne("RIBEIRO DA SILVA", "Baptiste");

        this.personneViewModel.createPersonne(item);
    }

    // 3 - Update an personne
    private void updatePersonne(Personne personne) {
        this.personneViewModel.updatePersonne(personne);
    }

    // 3 - Delete an personne
    private void deletePersonne(Personne personne) {
        this.personneViewModel.deletePersonne(personne);
    }

    // -----------------
    // CONFIGURATION
    // -----------------

    private void configureSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(() -> updateUI(personnes));
    }

    // 1 - Configure item click on RecyclerView
    private void configureOnClickRecyclerView() {
        ItemClickSupport.addTo(recyclerView, R.layout.item_personne)
                .setOnItemClickListener((recyclerView, position, v) -> Log.e("I'm OK", "Position : " + position));
    }

    // 3 - Configure RecyclerView, Adapter, LayoutManager & glue it together
    private void configureRecyclerView() {
        // 3.1 - Reset list
        this.personnes = new ArrayList<>();
        // 3.2 - Create adapter passing the list of users
        this.adapter = new PersonneAdapter(this);
        // 3.3 - Attach the adapter to the recyclerview to populate items
        this.recyclerView.setAdapter(this.adapter);
        // 3.4 - Set layout manager to position the items
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    // 6 - Update the list of items
    private void updateItemsList(List<Personne> personnes) {
        this.adapter.updateData(personnes);
    }

    // -----------------
    // ACTION
    // -----------------

    private void updateUI(List<Personne> users) {
        personnes.clear();
        personnes.addAll(users);

        adapter.notifyDataSetChanged();

        swipeRefreshLayout.setRefreshing(false);
    }
}