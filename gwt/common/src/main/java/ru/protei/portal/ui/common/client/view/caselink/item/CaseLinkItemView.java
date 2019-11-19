package ru.protei.portal.ui.common.client.view.caselink.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.CaseInfo;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.ent.YouTrackIssueInfo;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.caselink.item.AbstractCaseLinkItemActivity;
import ru.protei.portal.ui.common.client.activity.caselink.item.AbstractCaseLinkItemView;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.Set;

import static ru.protei.portal.core.model.dict.En_CaseState.*;
import static ru.protei.portal.core.model.helper.CollectionUtils.setOf;
import static ru.protei.portal.test.client.DebugIds.DEBUG_ID_ATTRIBUTE;

public class CaseLinkItemView extends Composite implements AbstractCaseLinkItemView {

    public CaseLinkItemView() {
        initWidget(ourUiBinder.createAndBindUi(this));
        setTestAttributes();
    }

    @Override
    public void setActivity(AbstractCaseLinkItemActivity activity) {
        this.activity = activity;
    }

    @Override
    public void setValue(CaseLink value) {
        this.caseLink = value;
        switch (value.getType()) {
            case CRM: {
                if (value.getCaseInfo() != null) {
                    number.setText(lang.crmPrefix() + value.getCaseInfo().getCaseNumber().toString());
                    fillCaseInfo(value.getCaseInfo());
                }
                break;
            }
            case YT: {
                number.setText(value.getRemoteId());
                processYouTrackInfo(value.getYouTrackInfo());
                break;
            }
        }

        if (HelperFunc.isEmpty(value.getLink())) {
            number.addStyleName("without-link");
            header.addStyleName("without-link");
        } else {
            number.setHref(value.getLink());
            header.setHref(value.getLink());
        }
    }

    @Override
    public CaseLink getValue() {
        return caseLink;
    }

    @Override
    public void setEnabled(boolean enabled) {
        remove.setVisible(!enabled);
    }

    @UiHandler("remove")
    public void closeClick(ClickEvent event) {
        event.preventDefault();
        event.stopPropagation();
        if (!remove.isVisible()) {
            return;
        }
        activity.onRemoveClicked(this);
    }

    private void fillCaseInfo( CaseInfo value ) {
        En_CaseState state = En_CaseState.getById(value.getStateId());
        fillData( value.getName(), state);
        fillCompletionState(state);
    }

    private void processYouTrackInfo( YouTrackIssueInfo youTrackInfo ) {
        if (youTrackInfo == null) {
            return;
        }
        fillData( youTrackInfo.getSummary(), youTrackInfo.getCaseState());
        fillCompletionState(youTrackInfo.getCaseState());
    }

    private void fillCompletionState( En_CaseState caseState ) {
        if(doneStates.contains( caseState )) {
            addStyleName("case-link-completed");
        }
    }

    private void fillData(String name, En_CaseState caseState ) {
        header.setText( name );
        state.addClassName("state-" + caseState.toString().toLowerCase());
    }

    private void setTestAttributes() {
        remove.getElement().setAttribute(DEBUG_ID_ATTRIBUTE, DebugIds.ISSUE.LINK_REMOVE_BUTTON);
    }

    @UiField
    Lang lang;

    @UiField
    Anchor number;
    @UiField
    Anchor remove;
    @UiField
    Anchor header;
    @UiField
    Element state;
    @UiField
    HTMLPanel root;

    private Set<En_CaseState> doneStates = setOf( DONE, VERIFIED, CANCELED, CLOSED, SOLVED_DUP, SOLVED_FIX, SOLVED_NOAP, IGNORED );

    private CaseLink caseLink;
    private AbstractCaseLinkItemActivity activity;

    interface CaseLinkViewUiBinder extends UiBinder<HTMLPanel, CaseLinkItemView> {}
    private static CaseLinkViewUiBinder ourUiBinder = GWT.create(CaseLinkViewUiBinder.class);
}
