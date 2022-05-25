package ru.protei.portal.ui.project.client.activity.preview;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ProjectAccessType;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.ui.common.client.activity.commenthistory.AbstractCommentAndHistoryListView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.En_CustomerTypeLang;
import ru.protei.portal.ui.common.client.lang.En_PersonRoleTypeLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CompanyControllerAsync;
import ru.protei.portal.ui.common.client.service.RegionControllerAsync;
import ru.protei.portal.ui.common.client.util.LinkUtils;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.ui.project.client.util.AccessUtil.*;

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
        if (getAccessType(policyService, En_Privilege.PROJECT_VIEW) == En_ProjectAccessType.NONE) {
            fireEvent(new ErrorPageEvents.ShowForbidden(initDetails.parent));
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
    public void onProductLinkClicked(Long id) {
        fireEvent(new ProductEvents.ShowFullScreen(id));
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
        view.setHeaderHref( LinkUtils.makePreviewLink(Project.class, value.getId()) );
        view.setName( value.getName() );
        view.setCreatedBy(lang.createBy(value.getCreator().getDisplayShortName(), DateFormatter.formatDateTime(value.getCreated())));
        view.setState( value.getStateName() );
        view.setStateIconColor( project.getStateColor() );
        view.setDirections(joining(value.getProductDirectionEntityOptionList(), ", ", EntityOption::getDisplayText));
        view.setDescription( value.getDescription() == null ? "" : value.getDescription() );
        view.setRegion( value.getRegion() == null ? "" : value.getRegion().getDisplayText() );
        view.setCompany(value.getCustomer() == null ? "" : value.getCustomer().getCname());
        view.setContracts(emptyIfNull(value.getContracts()).stream().collect(Collectors.toMap(contract -> contract, contract -> LinkUtils.makePreviewLink(Contract.class, contract.getId()))));
        view.setPlatforms(emptyIfNull(value.getPlatforms()).stream().collect(Collectors.toMap(platform -> platform, platform -> LinkUtils.makePreviewLink(Platform.class, platform.getId()))));
        view.setSubcontractors(stream(value.getSubcontractors()).map(Company::getCname).collect(Collectors.joining(", ")));

        if( isNotEmpty(value.getTeam()) ) {
            StringBuilder teamBuilder = new StringBuilder();
            stream(value.getTeam())
                    .collect(Collectors.groupingBy(PersonProjectMemberView::getRole,
                            Collectors.mapping(PersonProjectMemberView::getDisplayName, Collectors.joining(", "))))
                    .forEach((role, team) ->
                            teamBuilder.append("<b>")
                                    .append(roleTypeLang.getName(role))
                                    .append("</b>: ")
                                    .append(team)
                                    .append("<br/>"));

            view.setTeam(teamBuilder.toString());
        } else {
            view.setTeam("");
        }

        view.setProducts( stream(value.getProducts()).collect(Collectors.toMap(DevUnit::getId, DevUnit::getName, (n1, n2) -> n1 + ", " + n2)));
        view.setCustomerType(customerTypeLang.getName(value.getCustomerType()));

        synchronizeProjectSla(project.getProjectSlas(), project.getCustomerId(), projectSlaList -> {
            view.slaInputReadOnly().setValue(projectSlaList);
            view.slaContainerVisibility().setVisible(isSlaContainerVisible(projectSlaList));
        });

        view.setTechnicalSupportValidity(project.getTechnicalSupportValidity() == null ? null : DateTimeFormat.getFormat("dd.MM.yyyy").format(project.getTechnicalSupportValidity()));
        view.setTechnicalSupportValidityVisible(project.getTechnicalSupportValidity() == null);
        view.setWorkCompletionDateLabelVisible(project.getWorkCompletionDate() == null);
        view.setPurchaseDateLabelVisible(project.getPurchaseDate() == null);
        view.setWorkCompletionDate(project.getWorkCompletionDate() == null ? null : DateTimeFormat.getFormat("dd.MM.yyyy").format(project.getWorkCompletionDate()));
        view.setPurchaseDate(project.getPurchaseDate() == null ? null : DateTimeFormat.getFormat("dd.MM.yyyy").format(project.getPurchaseDate()));
        view.setPauseDateValidity(project.getPauseDate() == null ? "" : lang.projectPauseDate(DateTimeFormat.getFormat("dd.MM.yyyy").format(new Date(project.getPauseDate()))));

        if (policyService.hasPrivilegeFor(En_Privilege.ISSUE_VIEW)) {
            fireEvent(new CaseLinkEvents.Show(view.getLinksContainer())
                    .withCaseId(project.getId())
                    .withCaseType(En_CaseType.PROJECT)
                    .readOnly());
        }
        else {
            view.getLinksContainer().clear();
        }

        view.getCommentsContainer().clear();
        view.getCommentsContainer().add(commentAndHistoryView.asWidget());
        CommentAndHistoryEvents.Show showComments = new CommentAndHistoryEvents.Show(
            commentAndHistoryView,
            value.getId(),
            En_CaseType.PROJECT,
            canAccessProject(policyService, En_Privilege.PROJECT_EDIT, value.getTeam()),
            value.getCreatorId()
        );
        showComments.isPrivateVisible = canAccessProjectPrivateElements(policyService, En_Privilege.PROJECT_VIEW, value.getTeam());
        showComments.isPrivateCase = false;
        showComments.isNewCommentEnabled = canAccessProject(policyService, En_Privilege.PROJECT_EDIT, value.getTeam());
        showComments.initiatorCompanyId = project.getCustomerId();
        showComments.isMentionEnabled = policyService.hasSystemScopeForPrivilege(En_Privilege.PROJECT_VIEW);
        fireEvent(showComments);

        fireEvent(new ProjectEvents.ShowProjectDocuments(view.getDocumentsContainer(), project.getId(), false));
    }

    private void synchronizeProjectSla(List<ProjectSla> currentProjectSlaList, Long companyId, Consumer<List<ProjectSla>> projectSlaListConsumer) {
        companyService.getCompanyImportanceItems(companyId, new FluentCallback<List<CompanyImportanceItem>>()
                .withSuccess(companyImportanceItems ->
                        projectSlaListConsumer.accept(toList(companyImportanceItems, companyImportanceItem -> getProjectSla(currentProjectSlaList, companyImportanceItem)))
                ));
    }

    private ProjectSla getProjectSla(List<ProjectSla> projectSlaList, CompanyImportanceItem companyImportanceItem) {
        return projectSlaList
                .stream()
                .filter(projectSla -> companyImportanceItem.getImportanceLevelId().equals(projectSla.getImportanceLevelId()))
                .findAny()
                .orElse(new ProjectSla(companyImportanceItem.getImportanceLevelId(), companyImportanceItem.getImportanceCode()));
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
    @Inject
    CompanyControllerAsync companyService;
    @Inject
    private AbstractCommentAndHistoryListView commentAndHistoryView;

    private Project project;

    private AppEvents.InitDetails initDetails;
}
