package ru.protei.portal.core.model.query;

import java.util.List;
import java.util.Objects;

public class CardBatchQuery extends BaseQuery {

    private List<Long> typeIds;

    private List<String> numbers;

    private List<String> articles;

    private List<Integer> amounts;

    public CardBatchQuery() {
    }

    public List<Long> getTypeIds() {
        return typeIds;
    }

    public void setTypeIds(List<Long> typeIds) {
        this.typeIds = typeIds;
    }

    public List<String> getNumbers() {
        return numbers;
    }

    public void setNumbers(List<String> numbers) {
        this.numbers = numbers;
    }

    public List<String> getArticles() {
        return articles;
    }

    public void setArticles(List<String> articles) {
        this.articles = articles;
    }

    public List<Integer> getAmounts() {
        return amounts;
    }

    public void setAmounts(List<Integer> amounts) {
        this.amounts = amounts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardBatchQuery cardQuery = (CardBatchQuery) o;
        return Objects.equals(typeIds, cardQuery.typeIds)
                && Objects.equals(numbers, cardQuery.numbers)
                && Objects.equals(articles, cardQuery.articles)
                && Objects.equals(amounts, cardQuery.amounts)
                && Objects.equals(searchString, cardQuery.searchString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeIds, numbers, articles, amounts, searchString);
    }
}
