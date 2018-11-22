package uqac.inf872.projet.imok.controllers.fragments;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import uqac.inf872.projet.imok.adapters.OKCardAdapter;
import uqac.inf872.projet.imok.api.OKCardHelper;
import uqac.inf872.projet.imok.base.BaseFragment;
import uqac.inf872.projet.imok.controllers.activities.OKCardActivity;
import uqac.inf872.projet.imok.models.OKCard;
import uqac.inf872.projet.imok.utils.ItemClickSupport;

public class ListOKCardFragment extends BaseFragment {

    // FOR DESIGN
    @BindView(R.id.fragment_menu_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.fragment_main_swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.fragment_menu_fab)
    FloatingActionButton floatingActionButton;

    //FOR DATA
    private OKCardAdapter OKCardAdapter;

    // Method that will create a new instance of ListOKCardFragment, and add data to its bundle.
    public static ListOKCardFragment newInstance() {

        // Create new fragment
        return new ListOKCardFragment();
    }

    private void configureRecyclerView() {
        this.OKCardAdapter = new OKCardAdapter(generateOptionsForAdapter(OKCardHelper.getOKCard()), Glide.with(this));
        this.recyclerView.setAdapter(this.OKCardAdapter);
        this.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        ItemClickSupport.addTo(recyclerView, R.layout.fragment_menu_ok_card_item)
                .setOnItemClickListener((rv, position, v) -> this.navigateToDetail(this.OKCardAdapter.getItem(position), v));
        this.swipeRefreshLayout.setOnRefreshListener(this::refreshOKCard);
    }

    // Create options for RecyclerView from a Query
    private FirestoreRecyclerOptions<OKCard> generateOptionsForAdapter(Query query) {
        return new FirestoreRecyclerOptions.Builder<OKCard>()
                .setQuery(query, OKCard.class)
                .setLifecycleOwner(this)
                .build();
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_menu;
    }

    @Override
    protected void configureDesign() {
        floatingActionButton.setOnClickListener(view -> createNew());
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

    private void createNew() {
        Intent intent = new Intent(getActivity(), OKCardActivity.class);
        startActivity(intent);
    }

    private void navigateToDetail(OKCard okCard, View viewClicked) {
        Intent intent = new Intent(getActivity(), OKCardActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString(OKCardActivity.BUNDLE_KEY_OK_CARD_ID, okCard.getId());
        bundle.putString(OKCardActivity.BUNDLE_KEY_OK_CARD_IMAGE_URL, okCard.getUrlPicture());
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