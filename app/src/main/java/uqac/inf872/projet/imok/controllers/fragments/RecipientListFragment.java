package uqac.inf872.projet.imok.controllers.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.OnClick;
import uqac.inf872.projet.imok.R;
import uqac.inf872.projet.imok.base.BaseFragment;
import uqac.inf872.projet.imok.controllers.activities.RecipientListActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecipientListFragment extends BaseFragment {

    // FOR DESIGN
    @BindView(R.id.fragment_detail_image)
    ImageView imageProject;

    @BindView(R.id.fragment_detail_title)
    TextView titleProject;

    @BindView(R.id.fragment_detail_description)
    TextView descriptionProject;

    @BindView(R.id.fragment_detail_share)
    Button shareButton;

    @BindView(R.id.fragment_detail_root_view)
    CoordinatorLayout rootView;

    @BindView(R.id.fragment_detail_fab)
    FloatingActionButton fabButton;

    // FOR DATA

    // -------------------
    // CONFIGURATION
    // -------------------

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_recipient_list;
    }

    @Override
    protected void configureDesign() {

    }

    @Override
    protected void updateDesign() {
        this.updateDesignWhenStarting();
    }

    // -------------------
    // ACTIONS
    // -------------------

    @OnClick(R.id.fragment_detail_share)
    public void onClickShareButton(View view) {
        this.showMessage();
    }

    // -------------------
    // DATA
    // -------------------

    // -------------------
    // UI
    // -------------------

    private void updateDesignWhenStarting() {
        Glide.with(this).load(this.getImageURLFromBundle()).into(this.imageProject);
        this.animateViews();
    }

    private void showMessage() {
        Snackbar snackbar = Snackbar.make(rootView, R.string.detail_fragment_snackbar_message, Snackbar.LENGTH_SHORT);
        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    private void animateViews() {
        this.alphaViewAnimation(this.titleProject, 200);
        this.scaleViewAnimation(this.fabButton, 500);
        this.alphaViewAnimation(this.descriptionProject, 400);
        this.fromBottomAnimation(this.shareButton, 800);
    }


    private void scaleViewAnimation(View view, int startDelay) {
        // Reset view
        view.setScaleX(0);
        view.setScaleY(0);
        // Animate view
        view.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setInterpolator(new FastOutSlowInInterpolator())
                .setStartDelay(startDelay)
                .setDuration(500)
                .start();
    }

    private void alphaViewAnimation(View view, int startDelay) {
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(1000);
        animation.setStartOffset(startDelay);
        view.startAnimation(animation);
    }

    private void fromBottomAnimation(View view, int startDelay) {
        Animation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        animation.setDuration(1000);
        animation.setInterpolator(new AccelerateInterpolator());
        animation.setStartOffset(startDelay);
        view.startAnimation(animation);
    }

    // -------------------
    // UTILS
    // -------------------

    private Integer getProjectIdFromBundle() {
        Bundle bundle = getActivity().getIntent().getExtras();
        return bundle.getInt(RecipientListActivity.BUNDLE_KEY_RECIPIENT_LIST_ID);
    }

    private String getImageURLFromBundle() {
        Bundle bundle = getActivity().getIntent().getExtras();
        return bundle.getString(RecipientListActivity.BUNDLE_KEY_RECIPIENT_LIST_IMAGE_URL);
    }
}
