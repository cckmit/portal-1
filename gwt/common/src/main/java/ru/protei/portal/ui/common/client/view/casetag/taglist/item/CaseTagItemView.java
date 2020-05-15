package ru.protei.portal.ui.common.client.view.casetag.taglist.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.inject.Inject;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.casetag.taglist.item.AbstractCaseTagItemActivity;
import ru.protei.portal.ui.common.client.activity.casetag.taglist.item.AbstractCaseTagItemView;

import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;
import static ru.protei.portal.ui.common.client.util.ColorUtils.makeContrastColor;
import static ru.protei.portal.ui.common.client.util.ColorUtils.makeSafeColor;

public class CaseTagItemView extends Composite implements AbstractCaseTagItemView {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
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
    public void setName(String name) {
        text.setText(name);
    }

    @Override
    public void setColor(String color) {
        String backgroundColor = makeSafeColor(color);
        String foregroundColor = makeContrastColor(backgroundColor);
        panel.getElement().getStyle().setProperty("backgroundColor", backgroundColor);
        panel.getElement().getStyle().setProperty("color", foregroundColor);
    }

    @UiHandler("remove")
    public void closeClick(ClickEvent event) {
        event.preventDefault();
        event.stopPropagation();
        if (activity != null) {
            activity.onTagDetach();
        }
    }

    private void ensureDebugIds() {
        remove.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.ISSUE.LINK_REMOVE_BUTTON);
    }

    @UiField
    HTMLPanel panel;
    @UiField
    Anchor remove;
    @UiField
    InlineLabel text;

    private AbstractCaseTagItemActivity activity;

    interface CaseLinkViewUiBinder extends UiBinder<HTMLPanel, CaseTagItemView> {}
    private static CaseLinkViewUiBinder ourUiBinder = GWT.create(CaseLinkViewUiBinder.class);
}
