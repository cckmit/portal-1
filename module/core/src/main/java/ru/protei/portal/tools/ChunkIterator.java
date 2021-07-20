package ru.protei.portal.tools;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_ResultStatus;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static ru.protei.portal.api.struct.Result.error;

public class ChunkIterator<T> implements Iterator<T> {
    private final BiFunction<Integer, Integer, Result<List<T>>> getWorkItems;
    private final Supplier<Boolean> isCancel;
    private final int limit;

    private int offset;
    private List<T> list;
    private int listNextIndex;
    private T next;
    private En_ResultStatus status = En_ResultStatus.OK;

    public ChunkIterator(BiFunction<Integer, Integer, Result<List<T>>> getWorkItems, Supplier<Boolean> isCancel, int limit) {
        this.getWorkItems = getWorkItems;
        this.isCancel = isCancel;
        this.limit = limit;
    }

    @Override
    public boolean hasNext() {
        if (list == null) {
            offset = 0;
            listNextIndex = 0;
            Result<List<T>> result = getList(offset, limit);
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
            Result<List<T>> result = getList(offset, limit);
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
    public T next() {
        if (next == null) {
            throw new NoSuchElementException();
        }
        T nextItem = this.next;
        this.next = null;
        return nextItem;
    }

    public En_ResultStatus getStatus() {
        return status;
    }

    private Result<List<T>> getList(int offset, int limit) {
        if (isCancel.get()) {
            return error(En_ResultStatus.CANCELED);
        }
        return getWorkItems.apply(offset, limit);
    }
}
