package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ContractState;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Date;

/**
 * Договор
 */
@JdbcEntity(table = "Contract")
public class Contract extends AuditableObject implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.EXPLICIT)
    private Long id;

    /**
     * Создатель анкеты
     */
    @JdbcJoinedColumn(localColumn = "id", remoteColumn = "id", mappedColumn = "CREATOR", table = "case_object", sqlTableAlias = "CO")
    private Long creatorId;

    /**
     * Дата создания
     */
    @JdbcJoinedColumn(localColumn = "id", table = "case_object", remoteColumn = "id", mappedColumn = "CREATED", sqlTableAlias = "CO")
    private Date created;

    /**
     * Дата изменения
     */
    @JdbcJoinedColumn(localColumn = "id", table = "case_object", remoteColumn = "id", mappedColumn = "MODIFIED", sqlTableAlias = "CO")
    private Date modified;

    /**
     * Менеджер
     */
    @JdbcJoinedColumn(localColumn = "id", table = "case_object", remoteColumn = "id", mappedColumn = "MANAGER", sqlTableAlias = "CO")
    private Long managerId;

    /**
     * Куратор договора
     */
    @JdbcJoinedColumn(localColumn = "id", table = "case_object", remoteColumn = "id", mappedColumn = "INITIATOR", sqlTableAlias = "CO")
    private Long curatorId;

    /**
     * Направление
     */
    @JdbcJoinedColumn(localColumn = "id", table = "case_object", remoteColumn = "id", mappedColumn = "product_id", sqlTableAlias = "CO")
    private Long directionId;

    /**
     * Сумма
     */
    private Long cost;

    /**
     * Текущее состояние договора
     */
    @JdbcJoinedColumn(localColumn = "id", table = "case_object", remoteColumn = "id", mappedColumn = "STATE", sqlTableAlias = "CO")
    private Integer stateId;

    /**
     * Номер
     */
    @JdbcJoinedColumn(localColumn = "id", table = "case_object", remoteColumn = "id", mappedColumn = "CASENO", sqlTableAlias = "CO")
    private String number;

    @Override
    public String getAuditType() {
        return "Contract";
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public En_ContractState getState () {
        return En_ContractState.getById(this.stateId);
    }

    public void setState (En_ContractState state) {
        this.stateId = state.getId();
    }

}

//        - дата подписания договора
//        - срок действия договора
//        - тип договора
//        - номер (case_object#caseno)
//        - Контрагент (селектор на компании)
//        - предмет договора  (case_object#des)
//        - сроки поставки и оплаты (тут срок может быть не один)
//        - куратор договора (селектор на сотрудников отдела договоров) - пока ограничений по должностям нет
