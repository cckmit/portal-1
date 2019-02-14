package ru.protei.portal.ui.contract.client.activity.preview;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.struct.ContractDates;
import ru.protei.portal.ui.common.client.events.CaseCommentEvents;
import ru.protei.portal.ui.common.client.events.ContractEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.*;
import ru.protei.portal.ui.common.client.service.ContractControllerAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.Date;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.HelperFunc.isNotEmpty;

public abstract class ContractPreviewActivity implements AbstractContractPreviewActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onShow( ContractEvents.ShowPreview event ) {
        event.parent.clear();
        event.parent.add( view.asWidget() );

        loadDetails(event.id);
    }

    private void loadDetails(Long id) {
        contractController.getContract(id, new RequestCallback<Contract>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errGetItem(), NotifyEvents.NotifyType.ERROR));
            }
            @Override
            public void onSuccess(Contract result) {
                if (result == null) {
                    onError(null);
                    return;
                }
                fillView(result);
            }
        });
    }

    private void fillView( Contract value ) {
        view.setHeader(lang.contractNum(value.getNumber()));
        view.setState(stateLang.getName(value.getState()));
        view.setType(typeLang.getName(value.getContractType()));
        view.setDateSigning(formatDate(value.getDateSigning()));
        view.setDateValid(formatDate(value.getDateValid()));
        view.setDescription(StringUtils.emptyIfNull(value.getDescription()));
        view.setContragent(StringUtils.emptyIfNull(value.getContragentName()));
        view.setManager(StringUtils.emptyIfNull(value.getManagerShortName()));
        view.setCurator(StringUtils.emptyIfNull(value.getCuratorShortName()));
        view.setDirection(StringUtils.emptyIfNull(value.getDirectionName()));
        view.setDates(getAllDatesAsString(value.getContractDates()));

        fireEvent( new CaseCommentEvents.Show( view.getCommentsContainer(), En_CaseType.CONTRACT, value.getId()) );
    }

    private String getAllDatesAsString(ContractDates dates) {
        if ( dates == null || dates.getItems() == null ) return "";
        return dates.getItems().stream()
                .map(p -> datesTypeLang.getName(p.getType()) + " – " + formatDate(p.getDate()) + (isNotEmpty(p.getComment()) ? " (" + p.getComment() + ")" : ""))
                .collect(Collectors.joining(", "));
    }

    private String formatDate(Date date) {
        return date == null ? lang.contractDateNotDefined() : dateFormat.format(date);
    }

    @Inject
    private Lang lang;
    @Inject
    private En_ContractStateLang stateLang;
    @Inject
    private En_ContractTypeLang typeLang;
    @Inject
    private En_ContractDatesTypeLang datesTypeLang;

    @Inject
    private AbstractContractPreviewView view;
    @Inject
    private ContractControllerAsync contractController;

    private DateTimeFormat dateFormat = DateTimeFormat.getFormat("dd.MM.yyyy");
}
