package ru.protei.portal.app.portal.server.portal1794;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.youtrack.dto.issue.IssueWorkItem;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;

class Iterator1794 implements Iterator<IssueWorkItem> {
    private final BiFunction<Integer, Integer, Result<List<IssueWorkItem>>> getWorkItems;
    private List<IssueWorkItem> list;
    private final int limit;
    private int offset;
    private IssueWorkItem next;
    private int listNextIndex;
    private En_ResultStatus status = En_ResultStatus.OK;

    public Iterator1794(BiFunction<Integer, Integer, Result<List<IssueWorkItem>>> getWorkItems, int limit) {
        this.getWorkItems = getWorkItems;
        this.limit = limit;
    }

    @Override
    public boolean hasNext() {
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
