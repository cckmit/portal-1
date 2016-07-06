package ru.protei.portal.tools.migrate.tools;

/**
 * Created by michael on 05.07.16.
 */
public interface EndOfBatch {

    public void onBatchEnd (Long lastIdValue);

}
