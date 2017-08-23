package ru.protei.portal.ui.official.client.view.preview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * Created by serebryakov on 23/08/17.
 */
public class OfficialPreviewView extends Composite {
    interface OfficialPreviewViewUiBinder extends UiBinder<HTMLPanel, OfficialPreviewView> {
    }

    private static OfficialPreviewViewUiBinder ourUiBinder = GWT.create(OfficialPreviewViewUiBinder.class);

    public OfficialPreviewView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }
}