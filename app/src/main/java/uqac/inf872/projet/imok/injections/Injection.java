package uqac.inf872.projet.imok.injections;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import uqac.inf872.projet.imok.api.BehanceService;
import uqac.inf872.projet.imok.repositories.CommentRepository;
import uqac.inf872.projet.imok.repositories.ProjectRepository;

/**
 * Created by Philippe on 27/02/2018.
 */

public class Injection {

    public static ProjectRepository provideProjectDataSource() {
        BehanceService behanceService = BehanceService.retrofit.create(BehanceService.class);
        return new ProjectRepository(behanceService);
    }

    public static CommentRepository provideCommentDataSource() {
        BehanceService behanceService = BehanceService.retrofit.create(BehanceService.class);
        return new CommentRepository(behanceService);
    }

    private static Executor provideExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    public static ViewModelFactory provideViewModelFactory() {
        ProjectRepository dataSourceProject = provideProjectDataSource();
        CommentRepository dataSourceComment = provideCommentDataSource();

        return new ViewModelFactory(dataSourceProject, dataSourceComment);
    }

//    public static ViewModelFactory provideViewModelFactory(Context context) {
//        ItemDataRepository dataSourceItem = provideItemDataSource(context);
//        UserDataRepository dataSourceUser = provideUserDataSource(context);
//
//        Executor executor = provideExecutor();
//
//        return new ViewModelFactory(dataSourceItem, dataSourceUser, executor);
//    }
}
