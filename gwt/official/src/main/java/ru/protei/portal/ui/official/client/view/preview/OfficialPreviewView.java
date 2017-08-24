package ru.protei.portal.ui.official.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import ru.protei.portal.ui.official.client.activity.preview.AbstractOfficialPreviewActivity;
import ru.protei.portal.ui.official.client.activity.preview.AbstractOfficialPreviewView;

/**
 * Created by serebryakov on 23/08/17.
 */
public class OfficialPreviewView extends Composite implements AbstractOfficialPreviewView{

    public OfficialPreviewView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractOfficialPreviewActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setCreationDate(String value) {

    }

    @Override
    public void setProduct(String value) {

    }

    @Override
    public void setRegion(String value) {

    }

    @Override
    public void setInfo(String value) {

    }

    @Override
    public void showFullScreen(boolean value) {
        this.fullScreen.setVisible( !value );
        if ( value ) {
            this.preview.addStyleName( "col-xs-12 col-lg-6" );
        } else {
            this.preview.setStyleName( "preview" );
        }
    }

    private AbstractOfficialPreviewActivity activity;

    interface OfficialPreviewViewUiBinder extends UiBinder<HTMLPanel, OfficialPreviewView> {}

    private static OfficialPreviewViewUiBinder ourUiBinder = GWT.create(OfficialPreviewViewUiBinder.class);
    @UiField
    HTMLPanel preview;
    @UiField
    Anchor fullScreen;
}