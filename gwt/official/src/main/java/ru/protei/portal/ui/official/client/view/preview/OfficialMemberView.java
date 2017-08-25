package ru.protei.portal.ui.official.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import ru.protei.portal.core.model.ent.OfficialMember;
import ru.protei.portal.ui.official.client.activity.preview.AbstractOfficialMembersView;

/**
 * Created by serebryakov on 25/08/17.
 */
public class OfficialMemberView extends Composite implements AbstractOfficialMembersView{

    public OfficialMemberView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setCompanyName(String key) {
        companyName.setInnerText(key);
    }

    @Override
    public void addOfficialMember(OfficialMember member) {

        Document document = Document.get();

        LIElement liElement = document.createLIElement();
        liElement.addClassName("media");

        DivElement divElement = document.createDivElement();
        divElement.addClassName("media-left");
        liElement.appendChild(divElement);

        DivElement mainDiv = document.createDivElement();
        mainDiv.addClassName("media-body");
        mainDiv.setInnerText("Name: " + member.getMember().getFirstName() +
                " amplua: " + member.getAmplua() + " relations: " + member.getRelations());
        liElement.appendChild(mainDiv);
        members.appendChild(liElement);
    }

    @UiField
    AnchorElement companyName;

    @UiField
    DivElement members;

    interface OfficialMemberViewUiBinder extends UiBinder<HTMLPanel, OfficialMemberView> {}

    private static OfficialMemberViewUiBinder ourUiBinder = GWT.create(OfficialMemberViewUiBinder.class);
}
