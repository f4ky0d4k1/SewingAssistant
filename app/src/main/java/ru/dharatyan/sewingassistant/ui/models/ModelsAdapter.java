package ru.dharatyan.sewingassistant.ui.models;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

import ru.dharatyan.sewingassistant.R;
import ru.dharatyan.sewingassistant.model.entity.Article;
import ru.dharatyan.sewingassistant.model.entity.Model;
import ru.dharatyan.sewingassistant.ui.DeleteDialogFragment;
import ru.dharatyan.sewingassistant.util.diffutil.ArticleDiffUtilCallback;

public class ModelsAdapter extends PagedListAdapter<Model, ModelsAdapter.ViewHolder> {

    private final Fragment fragment;
    private final ModelsViewModel modelsViewModel;

    protected ModelsAdapter(@NonNull DiffUtil.ItemCallback<Model> diffCallback, Fragment fragment) {
        super(diffCallback);
        this.fragment = fragment;
        modelsViewModel = new ViewModelProvider(fragment).get(ModelsViewModel.class);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_models, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(Objects.requireNonNull(getItem(position)));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView modelNameEdit;
        private final ArticlesAdapter articlesAdapter;
        private Model model;
        private LiveData<PagedList<Article>> articleLiveDataPagedList;

        ViewHolder(View view) {
            super(view);

            modelNameEdit = view.findViewById(R.id.text_model_name);
            Button buttonDelete = view.findViewById(R.id.button_model_delete);
            Button buttonShow = view.findViewById(R.id.button_articles_show);
            RecyclerView recyclerViewArticles = view.findViewById(R.id.recyclerView_articles);
            EditText articleNameEdit = view.findViewById(R.id.text_article_name);
            Button buttonArticleSave = view.findViewById(R.id.button_article_create);
            ConstraintLayout layout = view.findViewById(R.id.layout_articles);
            layout.setVisibility(View.GONE);

            buttonArticleSave.setOnClickListener(v ->
                    modelsViewModel.saveArticle(new Article(null, articleNameEdit.getText().toString(), model.getId())));

            modelNameEdit.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_DOWN &&
                        keyCode == KeyEvent.KEYCODE_ENTER) {
                    String name = modelNameEdit.getText().toString();
                    if (!name.equals(model.getName())) {
                        modelsViewModel.saveModel(new Model(model.getId(), name));
                        modelNameEdit.setText(model.getName());
                    }
                }
                return false;
            });

            articlesAdapter = new ArticlesAdapter(new ArticleDiffUtilCallback(), fragment);
            recyclerViewArticles.setAdapter(articlesAdapter);
            recyclerViewArticles.setLayoutManager(new LinearLayoutManager(view.getContext()));

            buttonDelete.setOnClickListener(v -> {
                new DeleteDialogFragment(
                        (dialogInterface, i) -> modelsViewModel.deleteModelById(model.getId()),
                        (dialogInterface, i) -> dialogInterface.cancel())
                        .show(fragment.getParentFragmentManager(), "deleteDialog");
            });

            buttonShow.setOnClickListener(v -> {
                if (layout.getVisibility() == View.VISIBLE)
                    layout.setVisibility(View.GONE);
                else layout.setVisibility(View.VISIBLE);
            });
        }

        public void bind(Model model) {
            this.model = model;
            modelNameEdit.setText(model.getName());

            if (articleLiveDataPagedList != null)
                articleLiveDataPagedList.removeObservers(fragment.getViewLifecycleOwner());
            articleLiveDataPagedList = modelsViewModel.getArticlesByModelId(model.getId());
            articleLiveDataPagedList.observe(fragment.getViewLifecycleOwner(), articlesAdapter::submitList);
        }

    }
}
