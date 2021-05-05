package ru.protei.portal.core.model.api;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import ru.protei.portal.core.model.ent.ProjectSla;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.view.PersonProjectMemberView;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static ru.protei.portal.core.model.dict.En_DevUnitPersonRoleType.HEAD_MANAGER;
import static ru.protei.portal.core.model.util.CrmConstants.State.PAUSED;

@JsonAutoDetect(
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        fieldVisibility = JsonAutoDetect.Visibility.ANY
)
public class ApiProject implements Serializable {

    private String name;

    private String description;

    private List<ProjectSla> projectSlas;

    private List<PersonProjectMemberView> team;

    private Long stateId;

    private Long regionId;

    private Long pauseDate;

    private Long companyId;

    private Integer customerTypeId;

    private Date technicalSupportValidity;

    private Date workCompletionDate;

    private Date purchaseDate;

    private Set<Long> directionsIds;

    private Set<Long> productsIds;

    private List<Long> subcontractorsIds;

    private List<Long> projectPlansIds;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<ProjectSla> getProjectSlas() {
        return projectSlas;
    }

    public List<PersonProjectMemberView> getTeam() {
        return team;
    }

    public Long getStateId() {
        return stateId;
    }

    public Long getRegionId() {
        return regionId;
    }

    public Long getPauseDate() {
        return pauseDate;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public Integer getCustomerTypeId() {
        return customerTypeId;
    }

    public Date getTechnicalSupportValidity() {
        return technicalSupportValidity;
    }

    public Date getWorkCompletionDate() {
        return workCompletionDate;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public Set<Long> getDirectionsIds() {
        return directionsIds;
    }

    public Set<Long> getProductsIds() {
        return productsIds;
    }

    public List<Long> getSubcontractorsIds() {
        return subcontractorsIds;
    }

    public List<Long> getProjectPlansIds() {
        return projectPlansIds;
    }

    public boolean isValid() {
        return StringUtils.isNotEmpty(name) &&
               CollectionUtils.isNotEmpty(productsIds) &&
               hasProjectManager(team) &&
               pauseDateFilled(stateId) &&
               companyId != null &&
               customerTypeId != null;
    }

    private boolean hasProjectManager(List<PersonProjectMemberView> team) {
        for (PersonProjectMemberView person : team) {
            if (person.getRole().equals(HEAD_MANAGER)) {
                return true;
            }
        }

        return false;
    }

    private boolean pauseDateFilled(Long stateId) {
        return stateId.equals(PAUSED) ? pauseDate != null : true;
    }

    @Override
    public String toString() {
        return "ApiProject{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", projectSlas=" + projectSlas +
                ", team=" + team +
                ", stateId=" + stateId +
                ", regionId=" + regionId +
                ", pauseDate=" + pauseDate +
                ", companyId=" + companyId +
                ", customerTypeId=" + customerTypeId +
                ", technicalSupportValidity=" + technicalSupportValidity +
                ", workCompletionDate=" + workCompletionDate +
                ", purchaseDate=" + purchaseDate +
                ", directionsIds=" + directionsIds +
                ", productsIds=" + productsIds +
                ", subcontractorsIds=" + subcontractorsIds +
                ", projectPlansIds=" + projectPlansIds +
                '}';
    }
}
