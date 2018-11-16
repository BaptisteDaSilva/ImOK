package uqac.inf872.projet.imok.repositories;

import io.reactivex.Observable;
import uqac.inf872.projet.imok.api.BehanceService;
import uqac.inf872.projet.imok.models.ApiResponse;

/**
 * Created by Philippe on 27/03/2018.
 */

public class ProjectRepository {

    private final BehanceService behanceService;

    public ProjectRepository(BehanceService behanceService) {
        this.behanceService = behanceService;
    }

    // ---

    public Observable<ApiResponse> getProjects(String request) {
        return this.behanceService.getProjects(request);
    }

    public Observable<ApiResponse> getProject(Integer projectID) {
        return this.behanceService.getProject(projectID);
    }
}
