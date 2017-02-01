package ru.protei.portal.core.model.query;

/**
 * Фильтр по оборудованию
 */
public class EquipmentQuery extends BaseQuery {

    private String name;

    private String classifierCode;

    private String pdraRegNum;

    private String pamrRegNum;

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getClassifierCode() {
        return classifierCode;
    }

    public void setClassifierCode( String classifierCode ) {
        this.classifierCode = classifierCode;
    }

    public String getPDRA_RegisterNumber() {
        return pdraRegNum;
    }

    public void setPdraRegNum( String pdraRegNum ) {
        this.pdraRegNum = pdraRegNum;
    }

    public String getPAMR_RegisterNumber() {
        return pamrRegNum;
    }

    public void setPamrRegNum( String pamrRegNum ) {
        this.pamrRegNum = pamrRegNum;
    }

    public EquipmentQuery() {
    }

    public EquipmentQuery( String name, String classifierCode, String pdraRegNum, String pamrRegNum ) {
        this.name = name;
        this.classifierCode = classifierCode;
        this.pdraRegNum = pdraRegNum;
        this.pamrRegNum = pamrRegNum;
    }
}
