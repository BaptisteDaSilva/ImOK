package uqac.inf872.projet.imok.injections;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import uqac.inf872.projet.imok.repositories.OKCardRepository;
import uqac.inf872.projet.imok.repositories.PositionRepository;
import uqac.inf872.projet.imok.repositories.RecipientListRepository;
import uqac.inf872.projet.imok.viewmodels.OKCardViewModel;
import uqac.inf872.projet.imok.viewmodels.PositionViewModel;
import uqac.inf872.projet.imok.viewmodels.RecipientListViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final OKCardRepository okCardRepository;
    private final PositionRepository positionRepository;
    private final RecipientListRepository recipientListRepository;

//    private final Executor executor;

    ViewModelFactory(OKCardRepository okCardRepository, PositionRepository positionRepository, RecipientListRepository recipientListRepository) {
        this.okCardRepository = okCardRepository;
        this.positionRepository = positionRepository;
        this.recipientListRepository = recipientListRepository;
    }

//    ViewModelFactory(OKCardRepository okCardRepository, PositionRepository positionRepository, RecipientListRepository recipientListRepository, Executor executor) {
//        this.okCardRepository = okCardRepository;
//        this.positionRepository = positionRepository;
//        this.recipientListRepository = recipientListRepository;
//        this.executor = executor;
//    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if ( modelClass.isAssignableFrom(OKCardViewModel.class) ) {
            return (T) new OKCardViewModel(okCardRepository);
        }
        if ( modelClass.isAssignableFrom(PositionViewModel.class) ) {
            return (T) new PositionViewModel(positionRepository);
        }
        if ( modelClass.isAssignableFrom(RecipientListViewModel.class) ) {
            return (T) new RecipientListViewModel(recipientListRepository);
        }

//        if (modelClass.isAssignableFrom(ItemViewModel.class)) {
//            return (T) new ItemViewModel(itemDataSource, userDataSource, executor);
//        }

        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}