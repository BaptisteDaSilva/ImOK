package uqac.inf872.projet.imok.injections;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import uqac.inf872.projet.imok.repositories.OKCardRepository;
import uqac.inf872.projet.imok.repositories.PositionRepository;
import uqac.inf872.projet.imok.repositories.RecipientListRepository;

public class Injection {

    private static OKCardRepository provideOKCardDataSource() {
        return new OKCardRepository();
    }

    private static PositionRepository providePositionDataSource() {
        return new PositionRepository();
    }

    private static RecipientListRepository provideRecipientListDataSource() {
        return new RecipientListRepository();
    }

    private static Executor provideExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    public static ViewModelFactory provideViewModelFactory() {
        OKCardRepository dataSourceOKCard = provideOKCardDataSource();
        PositionRepository dataSourcePosition = providePositionDataSource();
        RecipientListRepository dataSourceRecipientList = provideRecipientListDataSource();

        return new ViewModelFactory(dataSourceOKCard, dataSourcePosition, dataSourceRecipientList);
    }

//    public static ViewModelFactory provideViewModelFactory(Context context) {
//        OKCardRepository dataSourceOKCard = provideOKCardDataSource();
//        PositionRepository dataSourcePosition = providePositionDataSource();
//        RecipientListRepository dataSourceRecipientList = provideRecipientListDataSource();
//
//        Executor executor = provideExecutor();
//
//        return new ViewModelFactory(dataSourceOKCard, dataSourcePosition, dataSourceRecipientList, executor);
//    }
}
