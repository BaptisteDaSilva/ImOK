package uqac.inf872.projet.imok.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.Executor;

import uqac.inf872.projet.imok.models.Personne;
import uqac.inf872.projet.imok.repositories.PersonneDataRepository;

public class PersonneViewModel extends ViewModel {

    // REPOSITORIES
    private final PersonneDataRepository personneDataSource;
    private final Executor executor;


    public PersonneViewModel(PersonneDataRepository personneDataSource, Executor executor) {
        this.personneDataSource = personneDataSource;
        this.executor = executor;
    }

    // -------------
    // FOR PERSONNE
    // -------------

    public LiveData<List<Personne>> getPersonnes() {
        return personneDataSource.getPersonnes();
    }

    public LiveData<Personne> getPersonne(long userId) {
        return personneDataSource.getPersonne(userId);
    }

    public void createPersonne(Personne personne) {
        executor.execute(() -> {
            personneDataSource.createPersonne(personne);
        });
    }

    public void updatePersonne(Personne personne) {
        executor.execute(() -> {
            personneDataSource.updatePersonne(personne);
        });
    }

    public void deletePersonne(Personne personne) {
        executor.execute(() -> {
            personneDataSource.deletePersonne(personne);
        });
    }
}
