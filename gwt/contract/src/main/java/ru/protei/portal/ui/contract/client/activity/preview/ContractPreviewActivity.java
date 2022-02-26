package ru.protei.portal.ui.contract.client.activity.preview;

import com.google.gwt.dom.client.Element;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.ent.ContractDate;
import ru.protei.portal.core.model.ent.ContractSpecification;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.ui.common.client.activity.commenthistory.AbstractCommentAndHistoryListView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.MoneyRenderer;
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
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.joining;
import static ru.protei.portal.core.model.helper.CollectionUtils.stream;
import static ru.protei.portal.core.model.helper.HelperFunc.isNotEmpty;
import static ru.protei.portal.core.model.util.CrmConstants.State.CANCELLED;
import static ru.protei.portal.ui.common.shared.util.HtmlUtils.sanitizeHtml;

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
            fireEvent(new ErrorPageEvents.ShowForbidden());
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
        view.setHeader(sanitizeHtml(typeLang.getName(value.getContractType()) + " № " + value.getNumber()));
        view.setState(value.getStateId() != null
                ? "./images/contract_" + value.getStateName().toLowerCase() + ".png"
                : null);
        view.setDateSigning(formatDate(value.getDateSigning()));
        view.setDateValid(formatDate(value.getDateValid()));
        view.setDescription(sanitizeHtml(value.getDescription()));
        view.setContractor(value.getContractor() == null ? "" : sanitizeHtml(value.getContractor().getName()));
        view.setOrganization(sanitizeHtml(value.getOrganizationName()));
        view.setProjectManager(sanitizeHtml(value.getProjectManagerShortName()));
        view.setContractSignManager(sanitizeHtml(value.getContractSignManagerShortName()));
        view.setCurator(sanitizeHtml(value.getCuratorShortName()));
        view.setDirections(joining(value.getProductDirections(), ", ", direction -> sanitizeHtml(direction.getName())));
        view.setDates(getAllDatesAsWidget(value.getContractDates()));
        view.setSpecifications(getAllSpecificationsAsWidgets(value.getContractSpecifications()));
        view.setParentContract(value.getParentContractNumber() == null ? "" : lang.contractNum(value.getParentContractNumber()));
        view.setChildContracts(stream(value.getChildContracts())
                .filter(contract -> !Objects.equals(CANCELLED, contract.getStateId()))
                .map(contract -> sanitizeHtml(typeLang.getName(contract.getContractType()) + " " + contract.getNumber()))
                .collect(Collectors.joining(", ")));
        view.setProject(StringUtils.emptyIfNull(value.getProjectName()) + " (#" + value.getProjectId() + ")", LinkUtils.makePreviewLink(Project.class, value.getProjectId()));
        view.setDeliveryNumber(StringUtils.emptyIfNull(value.getDeliveryNumber()));
        view.setDateEndWarranty(formatDate(value.getDateEndWarranty()));
        view.setDateExecution(formatDate(value.getDateExecution()));

        fireEvent(new CaseTagEvents.ShowList(view.getTagsContainer(), En_CaseType.CONTRACT, contractId, true, a -> {}));
        view.getCommentsContainer().clear();
        view.getCommentsContainer().add(commentAndHistoryView.asWidget());
        fireEvent(new CommentAndHistoryEvents.Show(commentAndHistoryView, value.getId(), En_CaseType.CONTRACT, true, value.getCreatorId()));
    }

    private List<Widget> getAllDatesAsWidget(List<ContractDate>  dates) {
        if ( dates == null ) return null;
        return dates.stream()
                .map(p -> {
                    HTMLPanel root = new HTMLPanel("div", "");
                    Element b = DOM.createElement("b");
                    b.setInnerText( datesTypeLang.getName(p.getType()));
                    root.getElement().appendChild(b);
                    root.add(new InlineLabel(" - " + formatDate(p.getDate())
                            + (isNotEmpty(p.getComment()) ? " (" + p.getComment() + ")" : "")
                            + ((p.getCost() != null) ? (". " + lang.contractCost() + " - " + MoneyRenderer.getInstance().render(p.getCost()) + " "
                            + (p.getCurrency() != null ? p.getCurrency().getCode() : "")) : "")));
                    return root;
                })
                .collect(Collectors.toList());
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
    private AbstractCommentAndHistoryListView commentAndHistoryView;
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
