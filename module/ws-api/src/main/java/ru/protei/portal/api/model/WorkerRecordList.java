package ru.protei.portal.api.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name="workers")
public class WorkerRecordList {

    private List<WorkerRecord> workerRecords = new ArrayList<>();

    public  WorkerRecordList() {}

    @XmlElement(name="worker")
    public List<WorkerRecord> getWorkerRecords() {
        return workerRecords;
    }

    public void setWorkerRecords(List<WorkerRecord> workerRecords) {
        this.workerRecords = workerRecords;
    }
}
