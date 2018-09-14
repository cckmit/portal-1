package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.view.WorkerEntryShortView;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Helper-класс для работы с WorkerEntry
 */
public class WorkerEntryFacade {

    List< WorkerEntryShortView > workerEntries;

    public WorkerEntryFacade( List< WorkerEntryShortView > workerEntries ) {
        this.workerEntries = workerEntries;
    }

    public Stream< WorkerEntryShortView > entryStream() {
        return this.workerEntries.stream();
    }

    public WorkerEntryShortView getMainEntry() {
        return entryStream().filter( WorkerEntryShortView::isMain ).findFirst().orElse( getFirstEntry() );
    }

    public WorkerEntryShortView getFirstEntry() {
        return entryStream().findFirst().orElse( null );
    }

    public List< WorkerEntryShortView > getSortedEntries() {
        workerEntries.sort( Comparator.comparing( WorkerEntryShortView::isMain ).reversed() );
        return workerEntries;
    }
}
