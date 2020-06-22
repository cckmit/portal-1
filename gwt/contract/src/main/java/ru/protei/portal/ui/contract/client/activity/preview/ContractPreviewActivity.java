package ru.protei.portal.ui.contract.client.activity.preview;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.ent.ContractDate;
import ru.protei.portal.core.model.ent.ContractSpecification;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.struct.Project;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.En_ContractDatesTypeLang;
import ru.protei.portal.ui.common.client.lang.En_ContractTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ContractControllerAsync;
import ru.protei.portal.ui.common.client.util.LinkUtils;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
import ru.protei.portal.ui.contract.client.widget.contractspecification.previewitem.ContractSpecificationPreviewItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.HelperFunc.isNotEmpty;

public abstract class ContractPreviewActivity implements AbstractContractPreviewActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onInit( AppEvents.InitDetails event ) {
        this.initDetails = event;
    }

    @Event
    public void onShow( ContractEvents.ShowPreview event ) {
        event.parent.clear();

        view.footerVisibility().setVisible(false);
        event.parent.add( view.asWidget() );

        loadDetails(event.id);
        view.isFullScreen(false);
    }

    @Event
    public void onShow(ContractEvents.ShowFullScreen event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.CONTRACT_VIEW)) {
            fireEvent(new ForbiddenEvents.Show());
            return;
        }

        initDetails.parent.clear();

        view.footerVisibility().setVisible(true);
        initDetails.parent.add(view.asWidget());

        loadDetails(event.contractId);
        view.isFullScreen(true);
    }

    @Override
    public void onFullScreenClicked() {
        fireEvent(new ContractEvents.ShowFullScreen(contractId));
    }

    @Override
    public void onGoToContractsClicked() {
        fireEvent(new ContractEvents.Show(true));
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
                contractId = result.getId();
                fillView(result);
            }
        });
    }

    private void fillView( Contract value ) {
        view.setHeader(lang.contractNum(value.getNumber()));
        String stateImage = null;
        if ( value.getState() != null ) {
            stateImage = "./images/contract_" + value.getState().name().toLowerCase() + ".png";
        }
        view.setState( stateImage );

        view.setType(typeLang.getName(value.getContractType()));
        view.setDateSigning(formatDate(value.getDateSigning()));
        view.setDateValid(formatDate(value.getDateValid()));
        view.setDescription(StringUtils.emptyIfNull(value.getDescription()));
        view.setContragent(value.getProjectId() == null ? StringUtils.emptyIfNull(value.getCaseContragentName()) : StringUtils.emptyIfNull(value.getContragentName()));
        view.setOrganization(StringUtils.emptyIfNull(value.getOrganizationName()));
        view.setManager(value.getProjectId() == null ? StringUtils.emptyIfNull(value.getCaseManagerShortName()) : StringUtils.emptyIfNull(value.getManagerShortName()));
        view.setCurator(StringUtils.emptyIfNull(value.getCuratorShortName()));
        view.setDirection(value.getProjectId() == null ? StringUtils.emptyIfNull(value.getCaseDirectionName()) : StringUtils.emptyIfNull(value.getDirectionName()));
        view.setDates(getAllDatesAsStringAsHTML(value.getContractDates()));
        view.setSpecifications(getAllSpecificationsAsWidgets(value.getContractSpecifications()));
        view.setParentContract(value.getParentContractNumber() == null ? "" : lang.contractNum(value.getParentContractNumber()));
        view.setChildContracts(CollectionUtils.stream(value.getChildContracts())
                .map(contract -> lang.contractNum(contract.getNumber()))
                .collect(Collectors.joining(", ")));
        view.setProject(StringUtils.emptyIfNull(value.getProjectName()), LinkUtils.makePreviewLink(Project.class, value.getProjectId()));

        fireEvent(new CaseCommentEvents.Show(view.getCommentsContainer())
                .withCaseType(En_CaseType.CONTRACT)
                .withCaseId(value.getId())
                .withModifyEnabled(true));
    }

    private String getAllDatesAsStringAsHTML(List<ContractDate> dates) {
        if ( dates == null ) return "";
        return dates.stream()
                .map(p -> "<div><b>" + datesTypeLang.getName(p.getType()) + "</b> – " + formatDate(p.getDate()) + (isNotEmpty(p.getComment()) ? " (" + p.getComment() + ")" : "" + "</div>"))
                .collect(Collectors.joining("\n"));
    }

    private List<ContractSpecificationPreviewItem> getAllSpecificationsAsWidgets(List<ContractSpecification> specifications) {
        if (specifications == null) return new ArrayList<>();
        return specifications.stream()
                .map(spec -> {
                    ContractSpecificationPreviewItem item = contractSpecificationPreviewItemProvider.get();
                    item.setValue(spec);
                    return item;
                })
                .collect(Collectors.toList());
    }

    private String formatDate(Date date) {
        return date == null ? lang.contractDateNotDefined() : dateFormat.format(date);
    }

    @Inject
    private Lang lang;
    @Inject
    private En_ContractTypeLang typeLang;
    @Inject
    private En_ContractDatesTypeLang datesTypeLang;

    @Inject
    private AbstractContractPreviewView view;
    @Inject
    private ContractControllerAsync contractController;
    @Inject
    private PolicyService policyService;

    @Inject
    private Provider<ContractSpecificationPreviewItem> contractSpecificationPreviewItemProvider;

    private Long contractId;

    private DateTimeFormat dateFormat = DateTimeFormat.getFormat("dd.MM.yyyy");
    private AppEvents.InitDetails initDetails;
}
