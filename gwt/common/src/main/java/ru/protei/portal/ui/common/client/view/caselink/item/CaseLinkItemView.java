package ru.protei.portal.ui.common.client.view.caselink.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.caselink.item.AbstractCaseLinkItemActivity;
import ru.protei.portal.ui.common.client.activity.caselink.item.AbstractCaseLinkItemView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.util.CaseStateUtils;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

public class CaseLinkItemView extends Composite implements AbstractCaseLinkItemView {


    public CaseLinkItemView() {
        initWidget(ourUiBinder.createAndBindUi(this));
        setTestAttributes();
    }

    @Override
    public void setActivity(AbstractCaseLinkItemActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setEnabled(boolean enabled) {
        remove.setVisible(enabled);
    }

    @Override
    public void setNumber(String value) {
        number.setText(value);
    }

    @Override
    public void setName(String value) {
        header.setText(value);
    }

    @Override
    public void setState(CaseState value) {
        if (value == null) return;
        state.addClassName("state-" + CaseStateUtils.makeStyleName(value.getState()));
        if ( value.isTerminal() ) {
            addStyleName("case-link-completed");
        }
    }

    @Override
    public void setHref(String link) {
        if (HelperFunc.isEmpty(link)) {
            number.addStyleName("without-link");
            header.addStyleName("without-link");
        } else {
            number.setHref(link);
            header.setHref(link);
        }
    }

    @Override
    public void setModel(CaseLink caseLink) {
        this.caseLink = caseLink;
    }

    @Override
    public CaseLink getModel() {
        return caseLink;
    }

    @UiHandler("remove")
    public void closeClick(ClickEvent event) {
        event.preventDefault();
        event.stopPropagation();
        if (!remove.isVisible()) {
            return;
        }
        activity.onRemoveClicked(this);
    }

    private void setTestAttributes() {
        remove.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.ISSUE.LINK_REMOVE_BUTTON);
    }

    @UiField
    Lang lang;

    @UiField
    Anchor number;
    @UiField
    Anchor remove;
    @UiField
    Anchor header;
    @UiField
    Element state;
    @UiField
    HTMLPanel root;

    private CaseLink caseLink;
    private AbstractCaseLinkItemActivity activity;

    interface CaseLinkViewUiBinder extends UiBinder<HTMLPanel, CaseLinkItemView> {}
    private static CaseLinkViewUiBinder ourUiBinder = GWT.create(CaseLinkViewUiBinder.class);
}
