package ru.protei.portal.ui.project.client.activity.preview;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_DevUnitPersonRoleType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.struct.ProjectInfo;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.En_CustomerTypeLang;
import ru.protei.portal.ui.common.client.lang.En_PersonRoleTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.RegionControllerAsync;
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

        this.projectId = event.projectId;

        fillView( projectId );
        view.watchForScroll( true );
        view.backButtonVisibility().setVisible( false );
    }

    @Event
    public void onShow( ProjectEvents.ShowFullScreen event ) {
        initDetails.parent.clear();
        initDetails.parent.add( view.asWidget() );

        this.projectId = event.projectId;

        fillView( projectId );
        view.backButtonVisibility().setVisible( true );
    }

    @Override
    public void onGoToProjectClicked() {
        fireEvent(new ProjectEvents.Show());
    }

    @Override
    public void onFullScreenPreviewClicked() {
        fireEvent( new ProjectEvents.ShowFullScreen( projectId ) );
    }

    @Override
    public void onProductLinkClicked() {
        if (project.getSingleProduct() != null) {
            fireEvent(new ProductEvents.ShowFullScreen(project.getSingleProduct().getId()));
        }
    }

    private void fillView(Long id ) {
        if (id == null) {
            fireEvent( new NotifyEvents.Show( lang.errIncorrectParams(), NotifyEvents.NotifyType.ERROR ) );
            return;
        }

        regionService.getProject( id, new RequestCallback<ProjectInfo>() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errNotFound(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( ProjectInfo project ) {
                fillView( project );
            }
        } );
    }

    private void fillView(ProjectInfo value) {
        this.project = value;
        view.setHeader( lang.projectHeader(value.getId().toString()) );
        view.setName( value.getName() );
        view.setAuthor( value.getCreator() == null ? "" : value.getCreator().getDisplayShortName() );
        view.setCreationDate( value.getCreated() == null ? "" : DateFormatter.formatDateTime( value.getCreated() )  );
        view.setState( value.getState().getId() );
        view.setDirection( value.getProductDirection() == null ? "" : value.getProductDirection().getDisplayText() );
        view.setDescription( value.getDescription() == null ? "" : value.getDescription() );
        view.setRegion( value.getRegion() == null ? "" : value.getRegion().getDisplayText() );
        view.setCompany(value.getCustomer() == null ? "" : value.getCustomer().getCname());

        if( value.getTeam() != null ) {
            StringBuilder teamBuilder = new StringBuilder();
            value.getTeam().stream()
                    .collect(Collectors.groupingBy(PersonProjectMemberView::getRole,
                            Collectors.mapping(PersonProjectMemberView::getDisplayShortName, Collectors.joining(", "))))
                    .forEach((role, team) ->
                            teamBuilder.append("<b>")
                                    .append(roleTypeLang.getName(role))
                                    .append("</b>: ")
                                    .append(team)
                                    .append("<br/>"));

            view.setTeam(teamBuilder.toString());
        }

        if (value.getProducts() != null && !value.getProducts().isEmpty()) {
            view.setProduct(value.getSingleProduct().getName());
        } else {
            view.setProduct("");
        }

        view.setCustomerType(customerTypeLang.getName(value.getCustomerType()));

        fireEvent(new CaseCommentEvents.Show.Builder(view.getCommentsContainer())
                .withCaseType(En_CaseType.PROJECT)
                .withCaseId(value.getId())
                .withModifyEnabled(policyService.hasEveryPrivilegeOf(En_Privilege.PROJECT_VIEW, En_Privilege.PROJECT_EDIT))
                .build());
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

    private Long projectId;
    ProjectInfo project;

    private AppEvents.InitDetails initDetails;
}
