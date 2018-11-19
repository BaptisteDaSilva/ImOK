package uqac.inf872.projet.imok.viewmodels;

import android.arch.lifecycle.ViewModel;

import uqac.inf872.projet.imok.repositories.PositionRepository;

public class PositionViewModel extends ViewModel {

    // REPOSITORIES
    private final PositionRepository positionRepository;

    // DATA

    public PositionViewModel(PositionRepository positionRepository) {
        this.positionRepository = positionRepository;
    }

    public void init() {
    }
}
