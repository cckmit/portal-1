package ru.protei.portal.ui.common.client.widget.selector.casetag;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.query.CaseTagQuery;
import ru.protei.portal.ui.common.client.events.CaseTagEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.LoadingHandler;
import ru.protei.portal.ui.common.client.selector.model.BaseSelectorModel;
import ru.protei.portal.ui.common.client.service.CaseTagControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.*;
import java.util.function.Consumer;

public abstract class CaseTagModel extends BaseSelectorModel<CaseTag> implements Activity
{

    @Event
    public void onTagChanged( CaseTagEvents.Changed event ) {
        changeElement( event.caseTag );
    }

    @Event
    public void onTagRemoved( CaseTagEvents.Removed event ) {
        removeElement( event.caseTag );
    }

    @Event
    public void onTagCreated( CaseTagEvents.Created event) {
        clean();
    }

    public void setCaseType(En_CaseType caseType) {
        this.caseType = caseType;
    }

    @Override
    protected void requestData( LoadingHandler selector, String searchText) {
        caseTagController.getTags(new CaseTagQuery(caseType), new FluentCallback<List<CaseTag>>()
                .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR)))
                .withSuccess(caseTags -> {
                    updateElements( caseTags, selector );
                })
        );
    }

    public void fillByIds( List<Long> tagIds, Consumer<List<CaseTag>> consumer ) {
        CaseTagQuery caseTagQuery = new CaseTagQuery();
        caseTagQuery.setIds( tagIds );
        caseTagController.getTags( caseTagQuery, new FluentCallback<List<CaseTag>>()
                .withError( throwable -> fireEvent( new NotifyEvents.Show( lang.errGetList(), NotifyEvents.NotifyType.ERROR ) ) )
                .withSuccess( caseTags -> {
                    consumer.accept( caseTags );
                } )
        );
    }

    @Inject
    CaseTagControllerAsync caseTagController;
    @Inject
    Lang lang;

    private En_CaseType caseType;

}
