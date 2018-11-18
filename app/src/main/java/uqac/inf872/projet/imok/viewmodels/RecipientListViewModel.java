package uqac.inf872.projet.imok.viewmodels;

import android.arch.lifecycle.ViewModel;

import uqac.inf872.projet.imok.repositories.RecipientListRepository;


public class RecipientListViewModel extends ViewModel {

    // REPOSITORIES
    private final RecipientListRepository recipientListRepository;

    // DATA

    public RecipientListViewModel(RecipientListRepository recipientListRepository) {
        this.recipientListRepository = recipientListRepository;
    }

    public void init() {
    }
}
