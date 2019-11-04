package ru.protei.portal.ui.common.client.widget.components.client.selector.search;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;

public class SearchField extends Composite {

    private SearchHandler searchHandler;

    public SearchField() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void setSearchHandler(SearchHandler searchHandler) {
        this.searchHandler = searchHandler;
    }

    @UiHandler("search")
    public void onSearchInputChanged(KeyUpEvent event) {
        searchHandler.onSearch(search.getText());
    }

    public String getSearchString() {
        return search.getText();
    }

    public void clearSearchText() {
        search.setValue(null);
    }

    @UiField
    HTMLPanel searchContainer;
    @UiField
    TextBox search;

    private static SearchFieldUiBinder ourUiBinder = GWT.create(SearchFieldUiBinder.class);


    interface SearchFieldUiBinder extends UiBinder<HTMLPanel, SearchField> {
    }

}