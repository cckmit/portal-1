package ru.protei.portal.core.report.ytwork;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.youtrack.dto.issue.IssueWorkItem;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import java.util.function.Supplier;

class ReportYtWorkIterator implements Iterator<IssueWorkItem> {
    private final BiFunction<Integer, Integer, Result<List<IssueWorkItem>>> getWorkItems;
    private final Supplier<Boolean> isCancel;
    private final int limit;

    private int offset;
    private List<IssueWorkItem> list;
    private int listNextIndex;
    private IssueWorkItem next;
    private En_ResultStatus status = En_ResultStatus.OK;

    public ReportYtWorkIterator(BiFunction<Integer, Integer, Result<List<IssueWorkItem>>> getWorkItems, Supplier<Boolean> isCancel, int limit) {
        this.getWorkItems = getWorkItems;
        this.isCancel = isCancel;
        this.limit = limit;
    }

    @Override
    public boolean hasNext() {
        if (isCancel.get()) {
            status = En_ResultStatus.CANCELED;
            return false;
        }
        if (list == null) {
            offset = 0;
            listNextIndex = 0;
            Result<List<IssueWorkItem>> result = getList(offset, limit);
            if (result.isOk()) {
                list = result.getData();
            } else {
                status = result.getStatus();
                return false;
            }
        }

        if (list.isEmpty()) {
            return false;
        }

        if (listNextIndex == list.size()) {
            offset += limit;
            listNextIndex = 0;
            Result<List<IssueWorkItem>> result = getList(offset, limit);
            if (result.isOk()) {
                list = result.getData();
                if (list.isEmpty()) {
                    return false;
                }
                next = list.get(listNextIndex++);
                return true;
            } else {
                status = result.getStatus();
                return false;
            }
        }
        next = list.get(listNextIndex++);
        return true;
    }

    @Override
    public IssueWorkItem next() {
        if (next == null) {
            throw new NoSuchElementException();
        }
        IssueWorkItem nextItem = this.next;
        this.next = null;
        return nextItem;
    }

    public En_ResultStatus getStatus() {
        return status;
    }

    private Result<List<IssueWorkItem>> getList(int offset, int limit) {
        return getWorkItems.apply(offset, limit);
    }
}
