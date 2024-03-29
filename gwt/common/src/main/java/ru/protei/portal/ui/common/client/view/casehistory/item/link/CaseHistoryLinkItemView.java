package ru.protei.portal.ui.common.client.view.casehistory.item.link;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;

public class CaseHistoryLinkItemView extends Composite {
    public CaseHistoryLinkItemView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void setLink(String name, String link) {
        setLink(name, link, null);
    }

    public void setLink(String name, String link, ClickHandler clickHandler) {
        if (link == null) {
            root.add(new InlineLabel(name));
            return;
        }

        Anchor anchor = new Anchor(name, link, "_blank");
        if (clickHandler != null){
            anchor.addClickHandler(clickHandler);
        }
        root.add(anchor);
    }

    @UiField
    HTMLPanel root;

    interface CaseHistorySimpleItemViewUiBinder extends UiBinder<HTMLPanel, CaseHistoryLinkItemView> {}
    private static CaseHistorySimpleItemViewUiBinder ourUiBinder = GWT.create(CaseHistorySimpleItemViewUiBinder.class);
}
