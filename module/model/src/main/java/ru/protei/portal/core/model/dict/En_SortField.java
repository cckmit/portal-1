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
    region_name("region_name"),

    /**
     * equipment project
     */
    project("project"),

    /**
     * equipment primary use
     */
    primary_use("primary_use"),

    /**
     * person birthday
     */
    birthday("date_format(birthday, '%m%d')");

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
