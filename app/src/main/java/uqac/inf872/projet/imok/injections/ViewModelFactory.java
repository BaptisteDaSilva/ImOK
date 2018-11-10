package uqac.inf872.projet.imok.injections;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import java.util.concurrent.Executor;

import uqac.inf872.projet.imok.repositories.PersonneDataRepository;
import uqac.inf872.projet.imok.view.PersonneViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final PersonneDataRepository personneDataSource;
    private final Executor executor;

    public ViewModelFactory(PersonneDataRepository personneDataSource, Executor executor) {
        this.personneDataSource = personneDataSource;
        this.executor = executor;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(PersonneViewModel.class)) {
            return (T) new PersonneViewModel(personneDataSource, executor);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}