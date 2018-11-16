package uqac.inf872.projet.imok.controllers.fragments;


import android.app.ActivityOptions;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import uqac.inf872.projet.imok.R;
import uqac.inf872.projet.imok.adapters.ProjectAdapter;
import uqac.inf872.projet.imok.base.BaseFragment;
import uqac.inf872.projet.imok.controllers.activities.DetailActivity;
import uqac.inf872.projet.imok.injections.Injection;
import uqac.inf872.projet.imok.injections.ViewModelFactory;
import uqac.inf872.projet.imok.models.ApiResponse;
import uqac.inf872.projet.imok.models.Project;
import uqac.inf872.projet.imok.utils.ItemClickSupport;
import uqac.inf872.projet.imok.viewmodels.ProjectViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends BaseFragment {

    public static final String REQUEST_ANDROID = "android";
    public static final String REQUEST_LOGO = "logo";
    public static final String REQUEST_LANDSCAPE = "landscape";

    // FOR DESIGN
    @BindView(R.id.fragment_menu_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.fragment_main_swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;
    //FOR DATA
    private ProjectViewModel projectViewModel;
    private ProjectAdapter projectAdapter;

    // -------------------
    // CONFIGURATION
    // -------------------

    private void configureViewModel() {
        ViewModelFactory mViewModelFactory = Injection.provideViewModelFactory();
        this.projectViewModel = ViewModelProviders.of(this, mViewModelFactory).get(ProjectViewModel.class);
        this.projectViewModel.init(REQUEST_ANDROID);
    }

    private void configureRecyclerView() {
        this.projectAdapter = new ProjectAdapter(new ArrayList<>(), Glide.with(this));
        this.recyclerView.setAdapter(this.projectAdapter);
        this.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        ItemClickSupport.addTo(recyclerView, R.layout.fragment_menu_item)
                .setOnItemClickListener((rv, position, v) -> this.navigateToDetail(this.projectAdapter.getProject(position), v));
        this.swipeRefreshLayout.setOnRefreshListener(() -> this.refreshProjects(REQUEST_ANDROID));
    }

    @Override
    protected BaseFragment newInstance() {
        return null;
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_menu;
    }

    @Override
    protected void configureDesign() {

    }

    @Override
    protected void updateDesign() {
        this.configureViewModel();
        this.configureRecyclerView();
        this.getProjects();
    }

    // -------------------
    // DATA
    // -------------------

    private void getProjects() {
        this.incrementIdleResource();
        this.disposable = this.projectViewModel.getProjects()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS)
                .subscribe(this::updateDesign, throwable -> Log.e("TAG", "ERROR: ", throwable));
    }

    private void refreshProjects(String request) {
        this.incrementIdleResource();
        this.disposable = this.projectViewModel.refreshProjects(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS)
                .subscribe(this::updateDesign, throwable -> Log.e("TAG", "ERROR: ", throwable));
    }

    // -------------------
    // UI
    // -------------------

    private void updateDesign(ApiResponse projectResponse) {

        this.decrementIdleResource();
        this.swipeRefreshLayout.setRefreshing(false);

        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_animation_fall_down);

        this.recyclerView.setLayoutAnimation(controller);
        this.projectAdapter.update(projectResponse.getProjects());
        this.recyclerView.scheduleLayoutAnimation();
    }

    public void updateDesignWhenUserClickedBottomView(String request) {
        this.refreshProjects(request);
    }

    // -------------------
    // NAVIGATION
    // -------------------

    private void navigateToDetail(Project project, View viewClicked) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(DetailActivity.BUNDLE_KEY_PROJECT_ID, project.getId());
        bundle.putString(DetailActivity.BUNDLE_KEY_PROJECT_IMAGE_URL, project.getCovers().getOriginal());
        intent.putExtras(bundle);

        // Animations
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), viewClicked, getString(R.string.animation_main_to_detail));
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }
}
