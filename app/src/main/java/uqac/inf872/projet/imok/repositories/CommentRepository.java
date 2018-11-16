package uqac.inf872.projet.imok.repositories;


import io.reactivex.Observable;
import uqac.inf872.projet.imok.api.BehanceService;
import uqac.inf872.projet.imok.models.ApiResponse;

/**
 * Created by Philippe on 27/03/2018.
 */

public class CommentRepository {

    private final BehanceService behanceService;

    public CommentRepository(BehanceService behanceService) {
        this.behanceService = behanceService;
    }

    // ---

    public Observable<ApiResponse> getComments(Integer projectId) {
        return this.behanceService.getComments(projectId);
    }
}
