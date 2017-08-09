package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.dict.En_DevUnitPersonRoleType;
import ru.protei.portal.core.model.dict.En_RegionState;
import ru.protei.portal.core.model.ent.AuditableObject;
import ru.protei.portal.core.model.ent.CaseLocation;
import ru.protei.portal.core.model.ent.CaseMember;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Информация о проекте в регионе
 */
public class ProjectInfo extends AuditableObject {

    /**
     * Идентификатор записи о проекте
     */
    private Long id;

    /**
     * Название проекта
     */
    private String name;

    /**
     * Детальный статус
     */
    private String details;

    /**
     * Текущее состояние проекта
     */
    private Long stateId;

    /**
     * продуктовое направление
     */
    EntityOption productDirection;

    /**
     * Руководитель
     */
    PersonShortView headManager;

    /**
     * Менеджеры внедрения
     */
    List<PersonShortView> managers;

    /**
     * Дата создания
     */
    Date created;

    EntityOption region;

    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails( String details ) {
        this.details = details;
    }

    public En_RegionState getState() {
        return En_RegionState.forId( stateId );
    }

    public void setState( En_RegionState state ) {
        this.stateId = state.getId();
    }

    public EntityOption getProductDirection() {
        return productDirection;
    }

    public void setProductDirection( EntityOption productDirection ) {
        this.productDirection = productDirection;
    }

    public PersonShortView getHeadManager() {
        return headManager;
    }

    public void setHeadManager( PersonShortView headManager ) {
        this.headManager = headManager;
    }

    public List<PersonShortView> getManagers() {
        return managers;
    }

    public void setManagers( List<PersonShortView> managers ) {
        this.managers = managers;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated( Date created ) {
        this.created = created;
    }

    public EntityOption getRegion() {
        return region;
    }

    public void setRegion( EntityOption region ) {
        this.region = region;
    }

    public static ProjectInfo fromCaseObject( CaseObject project ) {
        ProjectInfo projectInfo = new ProjectInfo();
        projectInfo.setId( project.getId() );
        projectInfo.setName( project.getName() );
        projectInfo.setDetails( project.getInfo() );
        projectInfo.setState( En_RegionState.forId( project.getStateId() ) );
        if ( project.getProduct() != null ) {
            projectInfo.setProductDirection( new EntityOption(
                project.getProduct().getName(), project.getProduct().getId()
            ) );
        }

        List<PersonShortView> deployManagers = new ArrayList<>();
        projectInfo.setManagers( deployManagers );
        if ( project.getMembers() != null ) {
            for ( CaseMember member : project.getMembers() ) {
                if ( En_DevUnitPersonRoleType.HEAD_MANAGER.equals( member.getRole() ) ) {
                    projectInfo.setHeadManager( PersonShortView.fromPerson( member.getMember() ) );
                } else if ( En_DevUnitPersonRoleType.DEPLOY_MANAGER.equals( member.getRole() ) ) {
                    deployManagers.add( PersonShortView.fromPerson( member.getMember() ) );
                }
            }
        }

        projectInfo.setCreated( project.getCreated() );

        List<CaseLocation> locations = project.getLocations();
        if ( locations != null && !locations.isEmpty() ) {
            projectInfo.setRegion( EntityOption.fromLocation( locations.get( 0 ).getLocation() ) );
        }
        return projectInfo;
    }

    @Override
    public String getAuditType() {
        return "ProjectInfo";
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        ProjectInfo that = (ProjectInfo) o;

        if ( id != null ? !id.equals( that.id ) : that.id != null ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ProjectInfo{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", details='" + details + '\'' +
            ", stateId=" + stateId +
            ", productDirection=" + productDirection +
            ", headManager=" + headManager +
            ", managers=" + managers +
            ", created=" + created +
            '}';
    }
}
