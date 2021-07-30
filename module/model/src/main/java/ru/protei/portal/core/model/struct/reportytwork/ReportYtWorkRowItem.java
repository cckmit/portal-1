package ru.protei.portal.core.model.struct.reportytwork;

import ru.protei.portal.core.model.dict.En_ReportYtWorkType;
import ru.protei.portal.core.model.view.WorkerEntryShortView;

import java.util.HashMap;
import java.util.Map;

public class ReportYtWorkRowItem implements ReportYtWorkRow {
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

    public Long getAllTimeSpent() {
        return allTimeSpent;
    }

    public void addAllTimeSpent(long time) {
        this.allTimeSpent = this.allTimeSpent + time;
    }

    public Map<String, Long> selectSpentTimeMap(En_ReportYtWorkType type) {
        switch (type) {
            case NIOKR: return this.getNiokrSpentTime();
            case NMA: return this.getNmaSpentTime();
            case CONTRACT: return this.getContractSpentTime();
            case GUARANTEE: return this.getGuaranteeSpentTime();
            default: return null;
        }
    }

    static public class PersonInfo {
        final private String displayName;
        final private WorkerEntryShortView mainWorkEntry;

        public PersonInfo(String displayName, WorkerEntryShortView mainWorkEntry) {
            this.displayName = displayName;
            this.mainWorkEntry = mainWorkEntry;
        }

        public String getDisplayName() {
            return displayName;
        }

        public WorkerEntryShortView getMainWorkEntry() {
            return mainWorkEntry;
        }
    }
}
