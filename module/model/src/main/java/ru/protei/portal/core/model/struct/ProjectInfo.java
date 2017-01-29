package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.dict.En_RegionState;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;

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
    PersonShortView headManager;

    /**
     * Менеджеры внедрения
     */
    List<EntityOption> managers;

    /**
     * Дата создания
     */
    Date created;

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
