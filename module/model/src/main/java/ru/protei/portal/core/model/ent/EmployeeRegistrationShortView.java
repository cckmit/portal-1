package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

public class EmployeeRegistrationShortView extends AuditableObject {
    public static final String AUDIT_TYPE = "EmployeeRegistrationEditRequest";

    private Long id;

    private Date employmentDate;

    private Set<PersonShortView> curators;

    private Set<Long> curatorIds;

    public EmployeeRegistrationShortView() {}

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getEmploymentDate() {
        return employmentDate;
    }

    public void setEmploymentDate(Date employmentDate) {
        this.employmentDate = employmentDate;
    }

    public Set<PersonShortView> getCurators() {
        return curators;
    }

    public void setCurators(Set<PersonShortView> curators) {
        this.curators = curators;
        this.curatorIds = curators.stream().map(PersonShortView::getId).collect(Collectors.toSet());
    }

    public Set<Long> getCuratorIds() {
        return curatorIds;
    }

    public void setCuratorIds(Set<Long> curatorIds) {
        this.curatorIds = curatorIds;
    }

    public static EmployeeRegistrationShortView fromEmployeeRegistration(EmployeeRegistration employeeRegistration) {
        if (employeeRegistration == null) {
            return null;
        }

        EmployeeRegistrationShortView employeeRegistrationShortView = new EmployeeRegistrationShortView();
        employeeRegistrationShortView.setId(employeeRegistration.getId());
        employeeRegistrationShortView.setCurators(CollectionUtils.emptyIfNull(employeeRegistration.getCurators()).stream().map(Person::toFullNameShortView).collect(Collectors.toSet()));

        return employeeRegistrationShortView;
    }

    @Override
    public String getAuditType() {
        return AUDIT_TYPE;
    }

    @Override
    public String toString() {
        return "EmployeeRegistrationShortView{" +
                "id=" + id +
                ", employmentDate=" + employmentDate +
                ", curators=" + curators +
                ", curatorIds=" + curatorIds +
                '}';
    }
}
