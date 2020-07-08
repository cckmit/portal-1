package ru.protei.portal.app.portal.client.view.profile;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.app.portal.client.activity.profile.AbstractProfilePageActivity;
import ru.protei.portal.app.portal.client.activity.profile.AbstractProfilePageView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.app.portal.client.widget.casefilter.group.PersonCaseFilterWidget;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.tab.TabWidget;

/**
 * Вид превью контакта
 */
public class ProfilePageView extends Composite implements AbstractProfilePageView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractProfilePageActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setName(String name) {
        this.name.setText(name);
    }

    @Override
    public void setCompany( String value ) {
        this.company.setInnerText(value);
    }

    @Override
    public void setIcon(String iconSrc) {
        this.icon.setSrc(iconSrc);
    }

    @Override
    public HasWidgets getGeneralContainer() {
        return generalContainer;
    }

    @Override
    public HasWidgets getSubscriptionsContainer() {
        return subscriptionsContainer;
    }

    @Override
    public void resetTabs() {
        tabs.selectFirstTab();
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        name.ensureDebugId(DebugIds.PROFILE.NAME);
        company.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.PROFILE.COMPANY);
    }

    @Inject
    @UiField
    Lang lang;
    @UiField
    InlineLabel name;
    @UiField
    Element company;
    @UiField
    ImageElement icon;
    @UiField
    HTMLPanel generalContainer;
    @UiField
    HTMLPanel subscriptionsContainer;
    @UiField
    TabWidget tabs;

    AbstractProfilePageActivity activity;

    interface ProfilePageViewUiBinder extends UiBinder<HTMLPanel, ProfilePageView> { }
    private static ProfilePageViewUiBinder ourUiBinder = GWT.create(ProfilePageViewUiBinder.class);
}