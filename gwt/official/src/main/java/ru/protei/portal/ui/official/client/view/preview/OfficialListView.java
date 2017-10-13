package ru.protei.portal.ui.official.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import ru.protei.portal.ui.official.client.activity.preview.AbstractOfficialListView;

/**
 * Представление списка должностных лиц
 */
public class OfficialListView extends Composite implements AbstractOfficialListView {

    public OfficialListView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setCompanyName(String key) {
        companyName.setInnerText(key);
    }

    @Override
    public HTMLPanel getItemContainer() {
        return itemContainer;
    }

    @UiField
    AnchorElement companyName;

    @UiField
    HTMLPanel itemContainer;

    interface OfficialMemberViewUiBinder extends UiBinder<HTMLPanel, OfficialListView> {}

    private static OfficialMemberViewUiBinder ourUiBinder = GWT.create(OfficialMemberViewUiBinder.class);
}
