package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.ent.WorkerEntry;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Helper-класс для работы с WorkerEntry
 */
public class WorkerEntryFacade {

    List< WorkerEntry > workerEntries;

    public WorkerEntryFacade( List< WorkerEntry > workerEntries ) {
        this.workerEntries = workerEntries;
    }

    public Stream< WorkerEntry > entryStream() {
        return this.workerEntries.stream();
    }

    public WorkerEntry getMainEntry() {
        return entryStream().filter( WorkerEntry::isMain ).findFirst().orElse( getFirstEntry() );
    }

    public WorkerEntry getFirstEntry() {
        return entryStream().findFirst().orElse( null );
    }

    public List< WorkerEntry > getSortedEntries() {
        workerEntries.sort( Comparator.comparing( WorkerEntry::isMain ).reversed() );
        return workerEntries;
    }
}
