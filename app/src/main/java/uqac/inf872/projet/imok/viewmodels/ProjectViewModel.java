package uqac.inf872.projet.imok.viewmodels;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;

import io.reactivex.Observable;
import uqac.inf872.projet.imok.models.ApiResponse;
import uqac.inf872.projet.imok.repositories.ProjectRepository;

/**
 * Created by Philippe on 27/03/2018.
 */

public class ProjectViewModel extends ViewModel {

    // REPOSITORIES
    private final ProjectRepository projectRepository;

    // DATA
    @Nullable
    private Observable<ApiResponse> currentProjects;

    public ProjectViewModel(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public void init(String projectId) {
        if ( this.currentProjects != null ) return;
        this.currentProjects = this.refreshProjects(projectId);
    }

    // ---

    public Observable<ApiResponse> getProjects() {
        return this.currentProjects;
    }

    public Observable<ApiResponse> refreshProjects(String projectId) {
        return projectRepository.getProjects(projectId);
    }
}
