package ru.dharatyan.sewingassistant.model.grouping;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.dharatyan.sewingassistant.model.entity.Article;
import ru.dharatyan.sewingassistant.model.entity.Date;
import ru.dharatyan.sewingassistant.model.entity.Model;

@Data
@AllArgsConstructor
public class OperationGroupingKey {

    private Date date;
    private Article article;
    private Model model;
}
