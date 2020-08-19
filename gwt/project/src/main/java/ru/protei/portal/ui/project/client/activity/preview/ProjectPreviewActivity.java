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
import ru.protei.portal.core.model.ent.ProjectSla;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.En_CustomerTypeLang;
import ru.protei.portal.ui.common.client.lang.En_PersonRoleTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.RegionControllerAsync;
import ru.protei.portal.ui.common.client.util.LinkUtils;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.emptyIfNull;

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
            fireEvent(new ErrorPageEvents.ShowForbidden());
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
        fireEvent(new ProjectEvents.Show(true));
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
        view.setContracts(emptyIfNull(value.getContracts()).stream().collect(Collectors.toMap(EntityOption::getDisplayText, contract -> LinkUtils.makePreviewLink(Contract.class, contract.getId()))));
        view.setPlatform(value.getPlatformName() == null ? "" : value.getPlatformName(), LinkUtils.makePreviewLink(Platform.class, value.getPlatformId()));

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
        view.slaInputReadOnly().setValue(project.getProjectSlas());
        view.slaContainerVisibility().setVisible(isSlaContainerVisible(project.getProjectSlas()));
        view.setTechnicalSupportValidity(project.getTechnicalSupportValidity() == null ? null : DateTimeFormat.getFormat("dd.MM.yyyy").format(project.getTechnicalSupportValidity()));

        Long pauseDate = project.getPauseDate();

        view.setPauseDateValidity(pauseDate == null ? null : DateTimeFormat.getFormat("dd.MM.yyyy").format(new Date(pauseDate)));
        view.pauseDateContainerVisibility().setVisible(pauseDate != null);
        view.setTechnicalSupportContainerView(pauseDate == null ? UiConstants.Styles.FULL_VIEW : UiConstants.Styles.SHORT_VIEW);

        if (policyService.hasPrivilegeFor(En_Privilege.ISSUE_VIEW)) {
            fireEvent(new CaseLinkEvents.Show(view.getLinksContainer())
                    .withCaseId(project.getId())
                    .withCaseType(En_CaseType.PROJECT)
                    .readOnly());
        }
        else {
            view.getLinksContainer().clear();
        }

        fireEvent(new CaseCommentEvents.Show( view.getCommentsContainer(), value.getId(), En_CaseType.PROJECT, policyService.hasPrivilegeFor( En_Privilege.PROJECT_EDIT ) ) );
        fireEvent(new ProjectEvents.ShowProjectDocuments(view.getDocumentsContainer(), project.getId(), false));
    }

    private boolean isSlaContainerVisible(List<ProjectSla> projectSlas) {
        if (CollectionUtils.isEmpty(projectSlas)) {
            return false;
        }

        if (projectSlas.stream().allMatch(ProjectSla::isEmpty)) {
            return false;
        }

        return true;
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
