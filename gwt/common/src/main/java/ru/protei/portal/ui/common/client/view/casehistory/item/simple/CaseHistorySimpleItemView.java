package ru.protei.portal.ui.common.client.view.casehistory.item.simple;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import ru.protei.portal.ui.common.client.util.LinkUtils;

public class CaseHistorySimpleItemView extends Composite {
    public CaseHistorySimpleItemView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void setLink(String name, String link) {
        if (link == null) {
            root.add(new InlineLabel(name));
            return;
        }

        root.add(new Anchor(name, link , "_blank"));
    }

    @UiField
    HTMLPanel root;

    interface CaseHistorySimpleItemViewUiBinder extends UiBinder<HTMLPanel, CaseHistorySimpleItemView> {}
    private static CaseHistorySimpleItemViewUiBinder ourUiBinder = GWT.create(CaseHistorySimpleItemViewUiBinder.class);
}
