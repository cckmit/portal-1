package ru.protei.portal.ui.official.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import ru.protei.portal.core.model.ent.OfficialMember;
import ru.protei.portal.ui.official.client.activity.preview.AbstractOfficialItemView;

/**
 * Created by serebryakov on 25/08/17.
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

    @UiField
    DivElement name;

    @UiField
    DivElement position;

    @UiField
    ParagraphElement relations;

    @UiField
    ParagraphElement amplua;

    interface OfficialItemViewUiBinder extends UiBinder<HTMLPanel, OfficialItemView> {}

    private static OfficialItemViewUiBinder ourUiBinder = GWT.create(OfficialItemViewUiBinder.class);
}