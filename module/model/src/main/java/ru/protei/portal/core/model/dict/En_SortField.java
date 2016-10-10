package ru.protei.portal.core.model.dict;

/**
 * Created by michael on 10.10.16.
 */
public enum En_SortField {

    /**
     * company name
     */
    comp_name("cname"),

    /**
     * product name (field from common table DEV_UNIT)
     */
    prod_name("unit_name"),

    /**
     * Group name (Company-group name for example)
     */
    group_name ("group_name"),

    /**
     * date of creation
     */
    creation_date ("created"),

    /**
     * date of the last modification
     */
    last_update ("last_update")
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
}
