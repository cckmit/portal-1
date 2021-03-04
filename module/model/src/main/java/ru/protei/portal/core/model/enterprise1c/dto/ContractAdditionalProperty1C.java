package ru.protei.portal.core.model.enterprise1c.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ContractAdditionalProperty1C {
    public static final String DIRECTION_PROPERTY_KEY_PROTEI = "dd4061c5-33d4-11eb-80f6-ac1f6b010112";
    public static final String DIRECTION_PROPERTY_KEY_PROTEI_ST = "a13710ec-33d5-11eb-80f6-ac1f6b010112";

    @JsonProperty("LineNumber")
    private String lineNumber;

    @JsonProperty("Свойство_Key")
    private String propertyKey;

    @JsonProperty("Значение")
    private String value;

    @JsonProperty("Значение_Type")
    private final String valueType = "Edm.String";

    public String getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getPropertyKey() {
        return propertyKey;
    }

    public void setPropertyKey(String propertyKey) {
        this.propertyKey = propertyKey;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ContractAdditionalProperty1C() {
    }

    public ContractAdditionalProperty1C(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ContractAdditionalProperty1C{" +
                "lineNumber=" + lineNumber +
                ", propertyKey='" + propertyKey + '\'' +
                ", value='" + value + '\'' +
                ", valueType='" + valueType + '\'' +
                '}';
    }
}
