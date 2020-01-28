package ru.protei.portal.core.model.struct;

public class MethodProfile {
    public long invokeCount = 0L;
    public long minTime = 0L;
    public long maxTime = 0L;
    public long average = 0L;

    public void updateTime(long executionTime) {
        invokeCount++;
        if (minTime > executionTime || minTime == 0) minTime = executionTime;
        if (maxTime < executionTime || maxTime == 0) maxTime = executionTime;
        average = average + (executionTime - average) / invokeCount;
    }
}
