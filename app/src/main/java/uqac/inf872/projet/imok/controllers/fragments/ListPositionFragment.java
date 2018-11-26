package uqac.inf872.projet.imok.controllers.fragments;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import uqac.inf872.projet.imok.R;
import uqac.inf872.projet.imok.adapters.PositionAdapter;
import uqac.inf872.projet.imok.api.PositionHelper;
import uqac.inf872.projet.imok.base.BaseFragment;
import uqac.inf872.projet.imok.controllers.activities.PositionGPSActivity;
import uqac.inf872.projet.imok.controllers.activities.PositionWIFIActivity;
import uqac.inf872.projet.imok.models.Position;
import uqac.inf872.projet.imok.utils.ItemClickSupport;

public class ListPositionFragment extends BaseFragment {

    // FOR DESIGN
    @BindView(R.id.fragment_menu_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.fragment_main_swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.fragment_menu_fab)
    FloatingActionButton floatingActionButton;

    //FOR DATA
    private PositionAdapter positionAdapter;

    // Method that will create a new instance of ListOKCardFragment, and add data to its bundle.
    public static ListPositionFragment newInstance() {

        // Create new fragment
        return new ListPositionFragment();
    }

    private void configureRecyclerView() {
        this.positionAdapter = new PositionAdapter(generateOptionsForAdapter(PositionHelper.getPosition()), Glide.with(this));
        this.recyclerView.setAdapter(this.positionAdapter);
        this.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        ItemClickSupport.addTo(recyclerView, R.layout.fragment_menu_position_item)
                .setOnItemClickListener((rv, position, v) -> this.navigateToDetail(this.positionAdapter.getItem(position), v));
        this.swipeRefreshLayout.setOnRefreshListener(this::refreshOKCard);
    }

    // Create options for RecyclerView from a Query
    private FirestoreRecyclerOptions<Position> generateOptionsForAdapter(Query query) {
        return new FirestoreRecyclerOptions.Builder<Position>()
                .setQuery(query, Position.class)
                .setLifecycleOwner(this)
                .build();
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_menu;
    }

    @Override
    protected void configureDesign() {
        floatingActionButton.setOnClickListener(view ->
                showChoiceTypePositionDialog());
    }

    private void showChoiceTypePositionDialog() {
        ChoiceTypePositionDialog choiceTypePositionDialog = new ChoiceTypePositionDialog();
        choiceTypePositionDialog.show(getFragmentManager(), "fragment_choice_type_position_dialog");
    }

    @Override
    protected void updateDesign() {
        this.configureRecyclerView();
    }

    // -------------------
    // UI
    // -------------------

    private void refreshOKCard() {
        this.swipeRefreshLayout.setRefreshing(false);

        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_animation_fall_down);

        this.recyclerView.setLayoutAnimation(controller);
        this.recyclerView.scheduleLayoutAnimation();
    }

    // -------------------
    // NAVIGATION
    // -------------------

    private void navigateToDetail(Position position, View viewClicked) {
        Intent intent;

        if ( position.isWifi() ) {
            intent = new Intent(getActivity(), PositionWIFIActivity.class);
            intent.putExtra(PositionWIFIActivity.BUNDLE_KEY_POSITION_WIFI_ID, position.getId());
        } else {
            intent = new Intent(getActivity(), PositionGPSActivity.class);
            intent.putExtra(PositionGPSActivity.BUNDLE_KEY_POSITION_GPS_ID, position.getId());
        }

        // Animations
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), viewClicked, getString(R.string.animation_main_to_detail));
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }
}