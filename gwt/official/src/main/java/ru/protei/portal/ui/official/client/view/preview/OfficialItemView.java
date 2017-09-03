package ru.protei.portal.ui.official.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import ru.protei.portal.ui.official.client.activity.preview.AbstractOfficialItemActivity;
import ru.protei.portal.ui.official.client.activity.preview.AbstractOfficialItemView;

/**
 * Абстрактное представление должностного лица
 */
public class OfficialItemView extends Composite implements AbstractOfficialItemView {

    public OfficialItemView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setName(String firstName) {
        name.setInnerText(firstName);
    }

    @Override
    public void setAmplua(String amplua) {
        this.amplua.setInnerText(amplua);
    }

    @Override
    public void setPosition(String position) {
        this.position.setInnerText(position);
    }

    @Override
    public void setRelations(String relations) {
        this.relations.setInnerText(relations);
    }

    @Override
    public void setActivity(AbstractOfficialItemActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setButtonsVisibility(boolean isVisible) {
        editButton.setVisible(isVisible);
        removeButton.setVisible(isVisible);
    }

    @UiHandler("editButton")
    public void onEditClicked(ClickEvent event) {
        event.preventDefault();
        if (activity != null) {
            activity.onEditClicked(this);
        }
    }

    @UiHandler("removeButton")
    public void onRemoveClicked(ClickEvent event) {
        if (activity != null) {
            activity.onRemoveClicked(this);
        }
    }

    private AbstractOfficialItemActivity activity;

    @UiField
    DivElement name;

    @UiField
    DivElement position;

    @UiField
    ParagraphElement relations;

    @UiField
    ParagraphElement amplua;

    @UiField
    Anchor editButton;

    @UiField
    Anchor removeButton;

    interface OfficialItemViewUiBinder extends UiBinder<HTMLPanel, OfficialItemView> {}

    private static OfficialItemViewUiBinder ourUiBinder = GWT.create(OfficialItemViewUiBinder.class);
}