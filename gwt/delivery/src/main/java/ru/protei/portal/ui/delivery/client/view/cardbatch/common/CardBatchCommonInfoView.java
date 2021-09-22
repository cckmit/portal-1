package ru.protei.portal.ui.delivery.client.view.cardbatch.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.markdown.MarkdownAreaWithPreview;
import ru.protei.portal.ui.common.client.widget.validatefield.ValidableTextBox;

public class CardBatchCommonInfoView extends Composite {

    @Inject
    public void init() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        ensureDebugIds();
    }

    public HasValue<String> number() {
        return number;
    }

    public HasValue<String> params(){
        return params;
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        number.ensureDebugId( DebugIds.ISSUE.NAME_INPUT );
        params.setEnsureDebugId( DebugIds.ISSUE.DESCRIPTION_INPUT );
    }

    @UiField
    Lang lang;

    @UiField
    ValidableTextBox number;
    @UiField
    MarkdownAreaWithPreview params;

    interface CommonUiBinder extends UiBinder<HTMLPanel, CardBatchCommonInfoView> {}
    private static CommonUiBinder ourUiBinder = GWT.create( CommonUiBinder.class );
}
