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
import uqac.inf872.projet.imok.adapters.RecipientListAdapter;
import uqac.inf872.projet.imok.api.RecipientListHelper;
import uqac.inf872.projet.imok.base.BaseFragment;
import uqac.inf872.projet.imok.controllers.activities.RecipientListActivity;
import uqac.inf872.projet.imok.models.RecipientList;
import uqac.inf872.projet.imok.utils.ItemClickSupport;

public class ListRecipientsListFragment extends BaseFragment {
    // FOR DESIGN
    @BindView(R.id.fragment_menu_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.fragment_main_swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.fragment_menu_fab)
    FloatingActionButton floatingActionButton;

    //FOR DATA
    private RecipientListAdapter recipientListAdapter;

    // Method that will create a new instance of ListOKCardFragment, and add data to its bundle.
    public static ListRecipientsListFragment newInstance() {

        // Create new fragment
        return new ListRecipientsListFragment();
    }

    private void configureRecyclerView() {
        this.recipientListAdapter = new RecipientListAdapter(generateOptionsForAdapter(RecipientListHelper.getRecipientList()), Glide.with(this));
        this.recyclerView.setAdapter(this.recipientListAdapter);
        this.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        ItemClickSupport.addTo(recyclerView, R.layout.fragment_menu_recipient_list_item)
                .setOnItemClickListener((rv, position, v) -> this.navigateToDetail(this.recipientListAdapter.getItem(position), v));
        this.swipeRefreshLayout.setOnRefreshListener(this::refreshOKCard);
    }

    // Create options for RecyclerView from a Query
    private FirestoreRecyclerOptions<RecipientList> generateOptionsForAdapter(Query query) {
        return new FirestoreRecyclerOptions.Builder<RecipientList>()
                .setQuery(query, RecipientList.class)
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
        Intent intent = new Intent(getActivity(), RecipientListActivity.class);
        startActivity(intent);
    }

    private void navigateToDetail(RecipientList recipientList, View viewClicked) {
        Intent intent = new Intent(getActivity(), RecipientListActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString(RecipientListActivity.BUNDLE_KEY_RECIPIENT_LIST_ID, recipientList.getId());
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