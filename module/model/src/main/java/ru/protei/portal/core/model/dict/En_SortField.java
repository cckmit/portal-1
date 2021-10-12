package ru.protei.portal.core.model.dict;

/**
 * Created by michael on 10.10.16.
 */
public enum En_SortField {

    /**
     *
     */
    id("id"),

    /**
     * company name
     */
    comp_name("cname"),

    /**
     * product name (field from common table DEV_UNIT)
     */
    prod_name("unit_name"),

    /**
     * group name (company-group name for example)
     */
    group_name ("group_name"),

    /**
     * category name (company-category name for example)
     */
    category_name ("category_name"),

    /**
     * date of creation
     */
    creation_date ("created"),

    /**
     * date of the last modification
     */
    last_update ("modified"),

    /**
     * person full name
     */
    person_full_name ("displayname"),

    /**
     * person position
     */
    person_position ("displayPosition"),

    /**
     * person IP address
     */
    employee_ip("ipAddress"),

    /**
     * issue number
     */
    issue_number ("caseno"),

    /**
     * field name from location
     */
    name("NAME"),

    /**
     * equipment sldwrks filename
     */
    equipment_name_sldwrks("name_sldwrks"),

    /**
     * user login
     */
    ulogin("ulogin"),

    /**
     * role name
     */
    role_name("role_code"),

    /**
     * region name
     */
    region_name("location_region_name"),

    /**
     * equipment project
     */
    equipment_project("case_object.case_name"),

    /**
     * equipment_decimal_number
     */
    equipment_decimal_number("decimal_view_sort_decimal"),

    /**
     * project
     */
    project("project"),

    /**
     * document project
     */
    document_project("case_object.case_name"),

    /**
     * equipment primary use
     */
    primary_use("primary_use"),

    /**
     * issue case_name
     */
    case_name("CASE_NAME"),

    /**
     * person birthday
     */
    birthday("date_format(birthday, '%m%d')"),

    /**
     * author id
     */
    author_id("author_id"),

    /**
     * project name
     */
    project_name("CASE_NAME"),

    /**
     * project number
     */
    project_number("id"),

    /**
     * project_creation_date
     */
    project_creation_date("CO.created"),

    /**
     * project_head_manager
     */
    project_head_manager("CM_MEMBER_ID"),

    /**
     * ip address
     */
    ip_address("INET_ATON(ip_address)"),

    /**
     * subnet address
     */
    address("INET_ATON(address)"),

    /**
     * last active IP-address date
     */
    active_date("last_active_date"),

    /**
     * state order
     */
    state_order("VIEW_ORDER"),

    /**
     * start_date
     */
    start_date("start_date"),

    /**
     * finish_date
     */
    finish_date("finish_date"),

    /**
     * absence date from
     */
    absence_date_from("from_time"),

    /**
     * absence date till
     */
    absence_date_till("till_time"),

    /**
     * absence person
     */
    absence_person("pa.displayname"),

    /**
     * absence reason
     */
    absence_reason("reason_id"),

    /**
     * room_reservation_date_from
     */
    room_reservation_date_from("room_reservation.date_from"),

    /**
     * Дата создания контракта
     */
    contract_creation_date("CO.created"),

    /**
     * Дата подписания контракта
     */
    contract_signing_date("contract.date_signing"),


    /**
     * Журнал дежурств date from
     */
    duty_log_date_from("duty_log.date_from"),

    /**
     * Журнал дежурств employee
     */
    duty_log_employee("duty_log.person_id"),

    /**
     * Журнал дежурств type
     */
    duty_log_type("duty_log.type"),

    /**
     * Просто значение
     */
    value("value"),

    /**
     * Сортировка по плану
     */
    by_plan("order_number"),

    /**
     * Сортировка по дням
     */
    day("day"),

    /**
     * Сортировка по ip сервера
     */
    server_ip("ip"),

    /**
     * Имя поставки
     */
    delivery_case_name("CO.CASE_NAME"),

    /**
     * Дата отправки поставки
     */
    delivery_departure_date("departure_date"),

    /**
     * Дата создания поставки
     */
    delivery_creation_date("CO.created"),

    /**
     * Серийный номер платы
     */
    card_serial_number("serial_number"),

    /**
     * Дата тестирования платы
     */
    card_test_date("test_date"),

    /**
     * Название типа платы
     */
    card_type_name("name"),

    /**
     * Тип платы партии плат
     */
    card_batch_type("CT.name"),

    /**
     * Номер партии плат
     */
    card_batch_number("number"),

    /**
     * Дедлайн партии плат
     */
    card_batch_deadline("deadline"),
    ;

    private String fieldName;
    private String bundleKey;

    En_SortField(String fieldName) {
        this(fieldName, "db_field_" + fieldName);
    }


    En_SortField(String fieldName, String bundleKey) {
        this.fieldName = fieldName;
        this.bundleKey = bundleKey;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getBundleKey() {
        return bundleKey;
    }


    public static En_SortField parse (String value, En_SortField def) {
        if (value == null || value.trim().isEmpty())
            return def;

        En_SortField f = En_SortField.valueOf(value.toLowerCase());
        return f != null ? f : def;
    }
}
