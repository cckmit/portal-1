package ru.protei.portal.ui.official.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.OfficialMember;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.official.client.activity.preview.AbstractOfficialPreviewActivity;
import ru.protei.portal.ui.official.client.activity.preview.AbstractOfficialPreviewView;

/**
 * Представление карточки должностных лиц
 */
public class OfficialPreviewView extends Composite implements AbstractOfficialPreviewView{

    public OfficialPreviewView() {
        initWidget(ourUiBinder.createAndBindUi(this));
        name.getElement().setPropertyString( "placeholder", lang.officialPreviewSearch() );
    }

    @Override
    public void setActivity(AbstractOfficialPreviewActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setCreationDate(String value) {
        creationDate.setInnerText(value);
    }

    @Override
    public void setProduct(String value) {
        product.setInnerText(value);
    }

    @Override
    public void setRegion(String value) {
        region.setInnerText(value);
    }

    @Override
    public void setInfo(String value) {
        info.setInnerHTML(value);
    }

    @Override
    public void showFullScreen(boolean value) {
        fullScreen.setVisible( !value );
        if ( value ) {
            preview.addStyleName( "col-xs-12 col-lg-6" );
        } else {
            preview.setStyleName( "preview" );
        }
    }

    @Override
    public void clearMembers() {
        membersContainer.clear();
    }

    @Override
    public HTMLPanel getMembersContainer() {
        return membersContainer;
    }

    @UiHandler( "fullScreen" )
    public void onFullScreenClicked ( ClickEvent event) {
        event.preventDefault();

        if ( activity != null ) {
            activity.onFullScreenClicked();
        }
    }

    private AbstractOfficialPreviewActivity activity;

    @UiField
    @Inject
    Lang lang;

    @UiField
    HTMLPanel preview;
    @UiField
    Anchor fullScreen;
    @UiField
    SpanElement region;
    @UiField
    SpanElement product;
    @UiField
    SpanElement creationDate;
    @UiField
    TextBox name;
    @UiField
    DivElement info;
    @UiField
    HTMLPanel membersContainer;

    private static OfficialPreviewViewUiBinder ourUiBinder = GWT.create(OfficialPreviewViewUiBinder.class);

    interface OfficialPreviewViewUiBinder extends UiBinder<HTMLPanel, OfficialPreviewView> {}
}