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
        Optional< WorkerEntry > entryOptional = entryStream().filter( WorkerEntry::isMain ).findFirst();
        if ( entryOptional.isPresent() )
            return entryOptional.get();
        return getFirstEntry();
    }

    public WorkerEntry getFirstEntry() {
        Optional< WorkerEntry > entryOptional = entryStream().findFirst();
        if ( entryOptional.isPresent() )
            return entryOptional.get();

        return null;
    }

    public List< WorkerEntry > getSortedEntries() {
        workerEntries.sort( Comparator.comparing( WorkerEntry::isMain ).reversed() );
        return workerEntries;
    }
}
