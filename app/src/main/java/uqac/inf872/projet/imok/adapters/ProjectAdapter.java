package uqac.inf872.projet.imok.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;

import java.util.List;

import uqac.inf872.projet.imok.R;
import uqac.inf872.projet.imok.adapters.viewholders.ProjectViewHolder;
import uqac.inf872.projet.imok.models.Project;

/**
 * Created by Philippe on 27/03/2018.
 */


public class ProjectAdapter extends RecyclerView.Adapter<ProjectViewHolder> {

    // FOR DATA
    private List<Project> projects;
    private RequestManager glide;

    public ProjectAdapter(List<Project> projects, RequestManager glide) {
        this.projects = projects;
        this.glide = glide;
    }

    @Override
    public ProjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_menu_item, parent, false);

        return new ProjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProjectViewHolder viewHolder, int position) {
        viewHolder.updateWithProject(this.getProject(position), this.glide);
    }

    @Override
    public int getItemCount() {
        return this.projects.size();
    }

    public Project getProject(int position) {
        return this.projects.get(position);
    }

    public void update(List<Project> projects) {
        this.projects = projects;
        this.notifyDataSetChanged();
    }
}
