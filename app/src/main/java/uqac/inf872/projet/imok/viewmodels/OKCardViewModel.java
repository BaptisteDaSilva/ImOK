package uqac.inf872.projet.imok.viewmodels;

import android.arch.lifecycle.ViewModel;

import uqac.inf872.projet.imok.repositories.OKCardRepository;

public class OKCardViewModel extends ViewModel {

    // REPOSITORIES
    private final OKCardRepository okCardRepository;

    // DATA

    public OKCardViewModel(OKCardRepository okCardRepository) {
        this.okCardRepository = okCardRepository;
    }

    public void init() {
    }
}
