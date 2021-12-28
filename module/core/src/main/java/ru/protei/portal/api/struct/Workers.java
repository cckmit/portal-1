package ru.protei.portal.api.struct;

import ru.protei.portal.core.model.ent.WorkerEntry;

import java.util.List;

public class Workers {
    List<WorkerEntry> workers;

    public Workers(List<WorkerEntry> workers) {
        this.workers = workers;
    }

    public String getActiveDepartment(String def) {
        WorkerEntry activeEntry = getActiveEntry();
        return activeEntry == null ? def : activeEntry.getDepartmentName();
    }

    public String getActivePosition(String def) {
        WorkerEntry activeEntry = getActiveEntry();
        return activeEntry == null ? def : activeEntry.getPositionName();
    }

    public String getAnyDepartment(String def) {
        WorkerEntry anyEntry = getAnyEntry();
        return anyEntry == null ? def : anyEntry.getDepartmentName();
    }

    public String getAnyPosition(String def) {
        WorkerEntry anyEntry = getAnyEntry();
        return anyEntry == null ? def : anyEntry.getPositionName();
    }

    private WorkerEntry getActiveEntry() {
        return workers == null ? null : workers.stream().filter(WorkerEntry::isMain).findFirst().orElse(null);
    }

    private WorkerEntry getAnyEntry() {
        return workers == null ? null : workers.stream().filter(WorkerEntry::isMain).findFirst().orElse(getFirstEntry());
    }

    private WorkerEntry getFirstEntry() {
        return workers == null ? null : workers.stream().findFirst().orElse(null);
    }
}
