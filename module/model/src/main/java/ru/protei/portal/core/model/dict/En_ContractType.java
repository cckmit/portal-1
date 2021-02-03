package ru.protei.portal.core.model.dict;

import ru.protei.winter.core.utils.enums.HasId;

/**
 * Типы договоров
 */
public enum En_ContractType implements HasId {

    /**
     * договор поставки
     */
    SUPPLY_CONTRACT(0),

    /**
     * договор на экспорт услуг
     */
    EXPORT_OF_SERVICE_CONTRACT(1),

    /**
     * договор на выполнение работ
     */
    WORK_CONTRACT(2),

    /**
     * договор на поставку и выполнение работ
     */
    SUPPLY_AND_WORK_CONTRACT(3),

    /**
     * лицензионный договор
     */
    LICENSE_CONTRACT(4),

    /**
     * договор аренды
     */
    LEASE_CONTRACT(5),

    /**
     * договор на закупку
     */
    PURCHASE_CONTRACT(6),

    /**
     * государственный контракт
     */
    GOVERNMENT_CONTRACT(7),

    /**
     * муниципальный контракт
     */
    MUNICIPAL_CONTRACT(8),

    /**
     * рамочный лицензионный договор
     */
    LICENSE_FRAMEWORK_CONTRACT(9),

    /**
     * рамочный договор поставки и выполнения работ
     */
    SUPPLY_AND_WORK_FRAMEWORK_CONTRACT(10),

    /**
     * рамочный договор поставки
     */
    SUPPLY_FRAMEWORK_CONTRACT(11),

    /**
     * договор ПСГО
      */
    AFTER_SALES_SERVICE_CONTRACT(12),

    /**
     * cубподрядный договор
     */
    SUBCONTRACT(13),

    /**
     * заказ
     */
    ORDER(14),

    /**
     * Заявка
     */
    REQUEST(16),

    /**
     * Доп соглашение
     */
    ADDITIONAL_AGREEMENT(17),

    /**
     * Договор
     */
    // agreement – чтобы хоть как-то отличалось от типа "контракт" (19) :)
    AGREEMENT(18),

    /**
     * Контракт
     */
    CONTRACT(19),

    /**
     * Договор на услуги
     */
    CONTRACT_FOR_SERVICE(20);

    En_ContractType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    private final int id;
}
