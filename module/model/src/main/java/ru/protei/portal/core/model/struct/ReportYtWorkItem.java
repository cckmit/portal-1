package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.ent.Person;

import java.util.HashMap;
import java.util.Map;

public class ReportYtWorkItem {
    private Person person;
    // список обработанных YtWorkItems для статистики / дебага
    final private Map<String, RepresentTime> issueSpentTime;
    // Map<Project, Map<NIOKR, SpentTime>>
    final private Map<String, Long> niokrSpentTime;
    // Map<Project, Map<NMA, SpentTime>>
    final private Map<String, Long> nmaSpentTime;
    // Map<Project, Map<CONTRACT, SpentTime>>
    final private  Map<String, Long> contractSpentTime;
    // Map<Project, Map<GUARANTEE, SpentTime>>
    final private Map<String, Long> guaranteeSpentTime;

    public ReportYtWorkItem() {
        this.issueSpentTime = new HashMap<>();
        this.niokrSpentTime = new HashMap<>();
        this.nmaSpentTime = new HashMap<>();
        this.contractSpentTime = new HashMap<>();
        this.guaranteeSpentTime = new HashMap<>();
    }

    public Map<String, RepresentTime> getIssueSpentTime() {
        return issueSpentTime;
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

    public void setPerson(Person person) {
        this.person = person;
    }

    static public class RepresentTime {
        final Long minutes;
        final String represent;

        public RepresentTime(Long minutes) {
            this.minutes = minutes;
            this.represent = makeRepresent(this.minutes);
        }

        public RepresentTime sum(RepresentTime other) {
            return new RepresentTime(this.minutes + other.minutes);
        }

        private String makeRepresent(Long minutes) {
            long h = minutes / 60;
            return String.format("%s ч. %s m.", h, minutes - h*60);
        }
    }
}
