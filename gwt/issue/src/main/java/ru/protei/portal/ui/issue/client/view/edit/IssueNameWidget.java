package ru.protei.portal.ui.issue.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.util.ClipboardUtils;
import ru.protei.portal.ui.issue.client.activity.edit.AbstractIssueEditActivity;

import java.util.function.Consumer;

public class IssueNameWidget extends Composite  {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
        copyNumberAndName.getElement().setAttribute( "title", lang.issueCopyNumberAndName() );

        ensureDebugIds();
    }

    public void setActivity( AbstractIssueEditActivity activity ) {
        this.activity = activity;
    }

    public void setName( String issueName ) {
        nameRO.setInnerHTML( issueName );
    }

    public void setCopyText (String copyText, Consumer<Boolean> callback) {
        if (copyNumberAndNameAddHandler != null) {
            copyNumberAndNameAddHandler.removeHandler();
        }
        copyNumberAndNameAddHandler = copyNumberAndName.addClickHandler(event -> callback.accept(ClipboardUtils.copyToClipboard(copyText)));
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }

        nameRO.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ISSUE.NAME_FIELD);
        copyNumberAndName.ensureDebugId( DebugIds.ISSUE.COPY_NUMBER_AND_NAME_BUTTON );
    }

    @UiField
    Lang lang;

    @UiField
    HeadingElement nameROPanel;
    @UiField
    Anchor copyNumberAndName;
    @UiField
    LabelElement nameRO;

    private AbstractIssueEditActivity activity;
    private HandlerRegistration copyNumberAndNameAddHandler;

    interface IssueNameWidgetUiBinder extends UiBinder<HTMLPanel, IssueNameWidget> {
    }

    private static IssueNameWidgetUiBinder ourUiBinder = GWT.create( IssueNameWidgetUiBinder.class );
}