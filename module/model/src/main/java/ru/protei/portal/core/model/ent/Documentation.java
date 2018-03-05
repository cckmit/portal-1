package ru.protei.portal.core.model.ent;

public class Documentation {
    private Long id;
    private String name;
    private String annotation;
    private String managerShortName;
    private String project;
    private DecimalNumber decimalNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public String getManagerShortName() {
        return managerShortName;
    }

    public void setManagerShortName(String managerShortName) {
        this.managerShortName = managerShortName;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public DecimalNumber getDecimalNumber() {
        return decimalNumber;
    }

    public void setDecimalNumber(DecimalNumber decimalNumber) {
        this.decimalNumber = decimalNumber;
    }
}
