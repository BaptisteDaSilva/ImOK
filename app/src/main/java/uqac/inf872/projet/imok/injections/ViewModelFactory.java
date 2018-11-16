package uqac.inf872.projet.imok.injections;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import uqac.inf872.projet.imok.repositories.CommentRepository;
import uqac.inf872.projet.imok.repositories.ProjectRepository;
import uqac.inf872.projet.imok.viewmodels.CommentsViewModel;
import uqac.inf872.projet.imok.viewmodels.ProjectDetailViewModel;
import uqac.inf872.projet.imok.viewmodels.ProjectViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final ProjectRepository projectRepository;
    private final CommentRepository commentRepository;
//    private final Executor executor;

    ViewModelFactory(ProjectRepository projectRepository, CommentRepository commentRepository) {
        this.projectRepository = projectRepository;
        this.commentRepository = commentRepository;
    }

//    ViewModelFactory(ItemDataRepository itemDataSource, UserDataRepository userDataSource, Executor executor) {
//        this.itemDataSource = itemDataSource;
//        this.userDataSource = userDataSource;
//        this.executor = executor;
//    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if ( modelClass.isAssignableFrom(ProjectViewModel.class) ) {
            return (T) new ProjectViewModel(projectRepository);
        }
        if ( modelClass.isAssignableFrom(ProjectDetailViewModel.class) ) {
            return (T) new ProjectDetailViewModel(projectRepository);
        }
        if ( modelClass.isAssignableFrom(CommentsViewModel.class) ) {
            return (T) new CommentsViewModel(commentRepository);
        }

//        if (modelClass.isAssignableFrom(ItemViewModel.class)) {
//            return (T) new ItemViewModel(itemDataSource, userDataSource, executor);
//        }

        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}