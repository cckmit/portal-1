package ru.protei.portal.core.model.struct.reportytwork;

import ru.protei.portal.core.model.dict.En_YoutrackWorkType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class ReportYtWorkRowItem implements ReportYtWorkRow {
    private PersonInfo personInfo;
    // Общее время
    private long allTimeSpent = 0L;
    // Map<Project, Map<NIOKR, SpentTime>>
    final private Map<String, Long> niokrSpentTime;
    // Map<Project, Map<NMA, SpentTime>>
    final private Map<String, Long> nmaSpentTime;
    // Map<Project, Map<CONTRACT, SpentTime>>
    final private  Map<String, Long> contractSpentTime;
    // Map<Project, Map<GUARANTEE, SpentTime>>
    final private Map<String, Long> guaranteeSpentTime;
    // Отработанное время
    private Optional<Integer> workedHours = Optional.empty();
    // Отработанное время на домашнюю компанию в Портале
    private Long homeCompanySpentTime = 0L;

    public ReportYtWorkRowItem() {
        this.niokrSpentTime = new HashMap<>();
        this.nmaSpentTime = new HashMap<>();
        this.contractSpentTime = new HashMap<>();
        this.guaranteeSpentTime = new HashMap<>();
    }

    public PersonInfo getPersonInfo() {
        return personInfo;
    }

    public Map<String, Long> getNiokrSpentTime() {
        return niokrSpentTime;
    }

    public Map<String, Long> getNmaSpentTime() {
        return nmaSpentTime;
    }

    public Map<String, Long> getContractSpentTime() {
        return contractSpentTime;
    }

    public Map<String, Long> getGuaranteeSpentTime() {
        return guaranteeSpentTime;
    }

    public void setPersonInfo(PersonInfo personInfo) {
        this.personInfo = personInfo;
    }

    public Optional<Integer> getWorkedHours() {
        return workedHours;
    }

    public void setWorkedHours(Integer workedHours) {
        this.workedHours = Optional.of(workedHours);
    }

    public Long getAllTimeSpent() {
        return allTimeSpent;
    }

    public void addAllTimeSpent(long time) {
        this.allTimeSpent += time;
    }

    public Long getHomeCompanySpentTime() {
        return homeCompanySpentTime;
    }

    public void addHomeCompanySpentTime(long time) {
        this.homeCompanySpentTime += time;
    }

    public Map<String, Long> selectSpentTimeMap(En_YoutrackWorkType type) {
        switch (type) {
            case NIOKR: return this.getNiokrSpentTime();
            case NMA: return this.getNmaSpentTime();
            case CONTRACT: return this.getContractSpentTime();
            case GUARANTEE: return this.getGuaranteeSpentTime();
            default: return null;
        }
    }

    @Override
    public String toString() {
        return "ReportYtWorkRowItem{" +
                "personInfo=" + personInfo +
                ", allTimeSpent=" + allTimeSpent +
                ", niokrSpentTime=" + niokrSpentTime +
                ", nmaSpentTime=" + nmaSpentTime +
                ", contractSpentTime=" + contractSpentTime +
                ", guaranteeSpentTime=" + guaranteeSpentTime +
                ", workedHours=" + workedHours +
                '}';
    }

    static public final class PersonInfo {
        static public final NameWithId nullCompanyName = new NameWithId("companyName", 0L);
        static public final NameWithId nullDepartmentParentName = new NameWithId("departmentParentName", 0L);
        static public final NameWithId nullDepartmentName = new NameWithId("departmentName", 0L);

        final private String displayName;
        final private Long personId;
        final private String workerId;
        final private String inn;
        final private NameWithId companyName;
        final private NameWithId departmentParentName;
        final private NameWithId departmentName;

        public PersonInfo(String displayName, Long personId, String workerId, String inn,
                          NameWithId companyName, NameWithId departmentParentName, NameWithId departmentName) {
            this.displayName = displayName;
            this.personId = personId;
            this.workerId = workerId;
            this.inn = inn;
            this.companyName = companyName;
            this.departmentParentName = departmentParentName;
            this.departmentName = departmentName;
        }

        public PersonInfo(String displayName, Long personId) {
            this(displayName, personId, null, null, nullCompanyName, nullDepartmentParentName, nullDepartmentName);
        }

        public String getDisplayName() {
            return displayName;
        }

        public Long getPersonId() {
            return personId;
        }

        public String getWorkerId() {
            return workerId;
        }

        public String getInn() {
            return inn;
        }

        public NameWithId getCompanyName() {
            return companyName;
        }

        public NameWithId getDepartmentParentName() {
            return departmentParentName;
        }

        public NameWithId getDepartmentName() {
            return departmentName;
        }

        public boolean hasWorkEntry() {
            return companyName != nullCompanyName;
        }

        @Override
        public String toString() {
            return "PersonInfo{" +
                    "displayName='" + displayName + '\'' +
                    ", personId=" + personId +
                    ", workerId='" + workerId + '\'' +
                    ", inn='" + inn + '\'' +
                    ", companyName=" + companyName +
                    ", departmentParentName=" + departmentParentName +
                    ", departmentName=" + departmentName +
                    '}';
        }
    }

    static public class NameWithId implements Comparable<NameWithId> {
        final private String string;
        final private long id;
        public NameWithId(String string, long id) {
            this.string = string;
            this.id = id;
        }
        public String getString() {
            return string;
        }
        public long getId() {
            return id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof NameWithId)) return false;
            NameWithId that = (NameWithId) o;
            return id == that.id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public int compareTo(NameWithId o) {
            return (int)(id - o.id);
        }

        @Override
        public String toString() {
            return "NameWithId{" +
                    "string='" + string + '\'' +
                    ", id=" + id +
                    '}';
        }
    }
}
