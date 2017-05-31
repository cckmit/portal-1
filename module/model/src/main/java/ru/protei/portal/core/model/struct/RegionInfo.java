package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.dict.En_RegionState;

import java.io.Serializable;

/**
 * Информация о регионе
 */
public class RegionInfo implements Serializable {

    /**
     * Идентификатор записи о регионе
     */
    public Long id;

    /**
     * Название региона
     */
    public String name;

    /**
     * Состояние региона
     */
    public En_RegionState state;

    /**
     * Дополнительная детальная информация о состоянии
     */
    public String details;

    /**
     * Номер региона
     */
    public Integer number;

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

    public En_RegionState getState() {
        return state;
    }

    public void setState( En_RegionState state ) {
        this.state = state;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails( String details ) {
        this.details = details;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber( Integer number ) {
        this.number = number;
    }
}
