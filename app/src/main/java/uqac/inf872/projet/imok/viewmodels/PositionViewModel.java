package uqac.inf872.projet.imok.viewmodels;

import android.arch.lifecycle.ViewModel;

import uqac.inf872.projet.imok.repositories.PositionRepository;

/**
 * Created by Philippe on 27/03/2018.
 */

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
