package ru.protei.portal.ui.project.client.activity.preview;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.ent.Platform;
import ru.protei.portal.core.model.struct.Project;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.En_CustomerTypeLang;
import ru.protei.portal.ui.common.client.lang.En_PersonRoleTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.RegionControllerAsync;
import ru.protei.portal.ui.common.client.util.LinkUtils;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.stream.Collectors;

/**
 * Активность превью проекта
 */
public abstract class ProjectPreviewActivity implements AbstractProjectPreviewActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity( this );
    }

    @Event
    public void onInit( AppEvents.InitDetails event ) {
        this.initDetails = event;
    }

    @Event
    public void onShow( ProjectEvents.ShowPreview event ) {
        event.parent.clear();
        event.parent.add( view.asWidget() );

        fillView( event.projectId );
        view.isFullScreen(false);
        view.backButtonVisibility().setVisible( false );
    }

    @Event
    public void onShow( ProjectEvents.ShowFullScreen event ) {
        if (!policyService.hasPrivilegeFor(En_Privilege.PROJECT_VIEW)) {
            fireEvent(new ForbiddenEvents.Show());
            return;
        }

        initDetails.parent.clear();
        initDetails.parent.add( view.asWidget() );

        fillView( event.projectId );
        view.isFullScreen(true);
        view.backButtonVisibility().setVisible( true );
    }

    @Override
    public void onGoToProjectClicked() {
        fireEvent(new ProjectEvents.Show());
    }

    @Override
    public void onFullScreenPreviewClicked() {
        if ( project == null ) return;
        fireEvent( new ProjectEvents.ShowFullScreen( project.getId() ) );
    }

    @Override
    public void onProductLinkClicked() {
        if (project.getSingleProduct() != null) {
            fireEvent(new ProductEvents.ShowFullScreen(project.getSingleProduct().getId()));
        }
    }

    private void fillView( Long id ) {
        if (id == null) {
            fireEvent( new NotifyEvents.Show( lang.errIncorrectParams(), NotifyEvents.NotifyType.ERROR ) );
            return;
        }

        regionService.getProject( id, new RequestCallback<Project>() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errNotFound(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( Project value ) {
                fillView( value );
            }
        } );
    }

    private void fillView(Project value) {
        this.project = value;

        view.setHeader( lang.projectHeader(value.getId().toString()) );
        view.setName( value.getName() );
        view.setCreatedBy(lang.createBy(value.getCreator().getDisplayShortName(), DateFormatter.formatDateTime(value.getCreated())));
        view.setState( value.getState().getId() );
        view.setDirection( value.getProductDirection() == null ? "" : value.getProductDirection().getDisplayText() );
        view.setDescription( value.getDescription() == null ? "" : value.getDescription() );
        view.setRegion( value.getRegion() == null ? "" : value.getRegion().getDisplayText() );
        view.setCompany(value.getCustomer() == null ? "" : value.getCustomer().getCname());
        view.setContract(value.getContractNumber() == null ? "" : lang.contractNum(value.getContractNumber()), LinkUtils.makeLink(Contract.class, value.getContractId()));
        view.setPlatform(value.getPlatformName() == null ? "" : value.getPlatformName(), LinkUtils.makeLink(Platform.class, value.getPlatformId()));

        if( value.getTeam() != null ) {
            StringBuilder teamBuilder = new StringBuilder();
            value.getTeam().stream()
                    .collect(Collectors.groupingBy(PersonProjectMemberView::getRole,
                            Collectors.mapping(PersonProjectMemberView::getName, Collectors.joining(", "))))
                    .forEach((role, team) ->
                            teamBuilder.append("<b>")
                                    .append(roleTypeLang.getName(role))
                                    .append("</b>: ")
                                    .append(team)
                                    .append("<br/>"));

            view.setTeam(teamBuilder.toString());
        }

        view.setProduct(value.getSingleProduct() == null ? "" : value.getSingleProduct().getName());
        view.setCustomerType(customerTypeLang.getName(value.getCustomerType()));
        view.setTechnicalSupportValidity(project.getTechnicalSupportValidity() == null ? null : DateTimeFormat.getFormat("dd.MM.yyyy").format(project.getTechnicalSupportValidity()));

        if (policyService.hasPrivilegeFor(En_Privilege.ISSUE_VIEW)) {
            fireEvent(new CaseLinkEvents.Show(view.getLinksContainer())
                    .withCaseId(project.getId())
                    .withCaseType(En_CaseType.PROJECT)
                    .withPageId(lang.projects())
                    .readOnly());
        }
        else {
            view.getLinksContainer().clear();
        }

        fireEvent(new CaseCommentEvents.Show(view.getCommentsContainer())
                .withCaseType(En_CaseType.PROJECT)
                .withCaseId(value.getId())
                .withModifyEnabled(policyService.hasPrivilegeFor(En_Privilege.PROJECT_EDIT)));
        fireEvent(new ProjectEvents.ShowProjectDocuments(view.getDocumentsContainer(), project.getId(), false));
    }

    @Inject
    Lang lang;
    @Inject
    En_PersonRoleTypeLang roleTypeLang;
    @Inject
    En_CustomerTypeLang customerTypeLang;
    @Inject
    AbstractProjectPreviewView view;
    @Inject
    RegionControllerAsync regionService;
    @Inject
    PolicyService policyService;

    private Project project;

    private AppEvents.InitDetails initDetails;
}
