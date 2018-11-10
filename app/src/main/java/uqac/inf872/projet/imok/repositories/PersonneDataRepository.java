package uqac.inf872.projet.imok.repositories;

import android.arch.lifecycle.LiveData;

import java.util.List;

import uqac.inf872.projet.imok.base.DAO.PersonneDAO;
import uqac.inf872.projet.imok.models.Personne;

public class PersonneDataRepository {

    private final PersonneDAO mPersonneDAO;

    public PersonneDataRepository(PersonneDAO mPersonneDAO) {
        this.mPersonneDAO = mPersonneDAO;
    }

    // --- GET ---

    public LiveData<List<Personne>> getPersonnes() {
        return this.mPersonneDAO.getPersonnes();
    }

    public LiveData<Personne> getPersonne(long id) {
        return this.mPersonneDAO.getPersonne(id);
    }

    // --- CREATE ---

    public void createPersonne(Personne personne) {
        mPersonneDAO.createPersonne(personne);
    }

    // --- UPDATE ---
    public void updatePersonne(Personne personne) {
        mPersonneDAO.updatePersonne(personne);
    }

    // --- DELETE ---
    public void deletePersonne(Personne personne) {
        mPersonneDAO.deletePersonne(personne);
    }
}