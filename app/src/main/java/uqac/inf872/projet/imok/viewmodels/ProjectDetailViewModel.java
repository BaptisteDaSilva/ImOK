package uqac.inf872.projet.imok.viewmodels;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;

import io.reactivex.Observable;
import uqac.inf872.projet.imok.models.ApiResponse;
import uqac.inf872.projet.imok.repositories.ProjectRepository;

/**
 * Created by Philippe on 27/03/2018.
 */

public class ProjectDetailViewModel extends ViewModel {

    // REPOSITORIES
    private final ProjectRepository projectRepository;

    // DATA
    @Nullable
    private Observable<ApiResponse> currentProject;

    public ProjectDetailViewModel(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public void init(Integer projectId) {
        if ( this.currentProject != null ) return;
        this.currentProject = this.projectRepository.getProject(projectId);
    }

    // ---

    public Observable<ApiResponse> getProject() {
        return this.currentProject;
    }
}
