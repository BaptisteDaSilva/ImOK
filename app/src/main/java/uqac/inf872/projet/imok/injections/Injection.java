package uqac.inf872.projet.imok.injections;

import android.content.Context;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import uqac.inf872.projet.imok.database.ImOKDatabase;
import uqac.inf872.projet.imok.repositories.PersonneDataRepository;

public class Injection {

    private static PersonneDataRepository providePersonneDataSource(Context context) {
        ImOKDatabase database = ImOKDatabase.getInstance(context);
        return new PersonneDataRepository(database.personneDao());
    }

    private static Executor provideExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    public static ViewModelFactory provideViewModelFactory(Context context) {
        PersonneDataRepository dataSourcePersonne = providePersonneDataSource(context);
        Executor executor = provideExecutor();
        return new ViewModelFactory(dataSourcePersonne, executor);
    }
}
