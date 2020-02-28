package ru.protei.portal.ui.common.client.view.casetag.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.casetag.item.AbstractCaseTagItemActivity;
import ru.protei.portal.ui.common.client.activity.casetag.item.AbstractCaseTagItemView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.util.ColorUtils;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

public class CaseTagItemView extends Composite implements AbstractCaseTagItemView {

    public CaseTagItemView() {
        initWidget(ourUiBinder.createAndBindUi(this));
        initDebugIds();
    }

    @Override
    public void setActivity(AbstractCaseTagItemActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setEnabled(boolean enabled) {
        remove.setVisible(enabled);
    }

    @Override
    public void setNameAndColor(String name, String color) {
        String backgroundColor = ColorUtils.makeSafeColor(color);
        String textColor = ColorUtils.makeContrastColor(backgroundColor);

        text.setText(name);

        panel.getElement().getStyle().setProperty("backgroundColor", backgroundColor);
        panel.getElement().getStyle().setProperty("color", textColor);
    }

    @Override
    public void setCaseTag( CaseTag caseTag) {
        this.caseTag = caseTag;
    }

    @Override
    public CaseTag getCaseTag() {
        return caseTag;
    }

    @UiHandler("remove")
    public void closeClick(ClickEvent event) {
        event.preventDefault();
        event.stopPropagation();

        activity.onDetachClicked(this);
    }

    private void initDebugIds() {
        remove.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.ISSUE.LINK_REMOVE_BUTTON);
    }

    @UiField
    Lang lang;

    @UiField
    HTMLPanel panel;
    @UiField
    Anchor remove;
    @UiField
    InlineLabel text;

    private CaseTag caseTag;
    private AbstractCaseTagItemActivity activity;

    interface CaseLinkViewUiBinder extends UiBinder<HTMLPanel, CaseTagItemView> {}
    private static CaseLinkViewUiBinder ourUiBinder = GWT.create(CaseLinkViewUiBinder.class);
}
