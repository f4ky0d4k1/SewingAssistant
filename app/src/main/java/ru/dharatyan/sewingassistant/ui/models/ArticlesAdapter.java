package ru.dharatyan.sewingassistant.ui.models;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;


import java.util.Objects;

import ru.dharatyan.sewingassistant.R;
import ru.dharatyan.sewingassistant.model.entity.Article;

public class ArticlesAdapter extends PagedListAdapter<Article, ArticlesAdapter.ViewHolder> {

    private final ModelsViewModel modelsViewModel;

    protected ArticlesAdapter(@NonNull DiffUtil.ItemCallback<Article> diffCallback, Fragment fragment) {
        super(diffCallback);
        modelsViewModel = new ViewModelProvider(fragment).get(ModelsViewModel.class);
    }

    @NonNull
    @Override
    public ArticlesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_articles, parent, false);
        return new ArticlesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(Objects.requireNonNull(getItem(position)));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameEdit;
        private Article article;

        ViewHolder(View view) {
            super(view);

            nameEdit = view.findViewById(R.id.text_article_name);
            Button buttonDelete = view.findViewById(R.id.button_article_delete);

            nameEdit.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_DOWN &&
                        keyCode == KeyEvent.KEYCODE_ENTER) {
                    String name = nameEdit.getText().toString();
                    if (!name.equals(article.getName())) {
                        modelsViewModel.saveArticle(new Article(article.getId(), name, article.getModelId()));
                        nameEdit.setText(article.getName());
                    }
                    return true;
                }
                return false;
            });

            buttonDelete.setOnClickListener(v -> modelsViewModel.deleteArticleById(article.getId()));
        }

        public void bind(Article article) {
            nameEdit.setText(article.getName());
            this.article = article;
        }
    }
}