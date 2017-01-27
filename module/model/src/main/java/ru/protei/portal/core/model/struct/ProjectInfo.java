package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.dict.En_RegionState;
import ru.protei.portal.core.model.view.EntityOption;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Информация о проекте в регионе
 */
public class ProjectInfo implements Serializable {

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
    EntityOption headManager;

    /**
     * Менеджеры внедрения
     */
    List<EntityOption> managers;

    /**
     * Дата создания
     */
    Date created;

    public static ProjectInfo make( Long id, String name, String details, En_RegionState state, String productDirection, String headManager, String... managers ) {
        ProjectInfo result = new ProjectInfo();
        result.id = id;
        result.name = name;
        result.details = details;
        result.setState( state );
        result.setProductDirection( new EntityOption( productDirection, null ) );
        result.setHeadManager( new EntityOption( headManager, null ) );
        if ( managers.length > 0 ) {
            result.managers = new ArrayList<>();
            for ( String manager : managers ) {
                result.managers.add( new EntityOption( manager, null ) );
            }
        }
        return result;
    }

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

    public EntityOption getHeadManager() {
        return headManager;
    }

    public void setHeadManager( EntityOption headManager ) {
        this.headManager = headManager;
    }

    public List<EntityOption> getManagers() {
        return managers;
    }

    public void setManagers( List<EntityOption> managers ) {
        this.managers = managers;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated( Date created ) {
        this.created = created;
    }
}
