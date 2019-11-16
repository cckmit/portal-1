package ru.protei.portal.ui.common.client.widget.components.client.cache;

import ru.brainworm.factory.widget.table.client.InfiniteLoadHandler;
import ru.protei.portal.ui.common.client.util.DataCache;
import ru.protei.portal.ui.common.client.widget.components.client.selector.LoadingHandler;

/**
 * Кеш для асинхронных селекторов
 * Запрос данных по необходимости чанками по ChunkSize, по умолчанию по 100 записей
 */
public class SelectorDataCache<T> implements DataCache.DataCacheHandler<T> {

    public SelectorDataCache() {
        cache = new DataCache<T>( this );
        cache.setChunkSize( 100 );
        cache.setSavedChunks( 100 );
    }

    @Override
    public void onDataCacheChanged() {
        if (loadingHandler != null) loadingHandler.onLoadingComplete();
    }

    public void setSavedChunks( int savedChunks ) {
        cache.setSavedChunks( savedChunks );
    }

    public void setLoadHandler( InfiniteLoadHandler<T> loadHandler ) {
        cache.setLoadHandler( loadHandler );
    }

    public T get( int elementIndex, LoadingHandler loadingHandler ) {
        if (total <= elementIndex) return null;
        T option = cache.get( elementIndex );
        if (option == null) {
            this.loadingHandler = loadingHandler;
            loadingHandler.onLoadingStart();
        }
        return option;
    }

    public void setTotal( int total ) {
        this.total = total;
    }

    public void clearCache(){
        cache.clearCache();
        total = Integer.MAX_VALUE;
    }

    private LoadingHandler loadingHandler;
    private int total = Integer.MAX_VALUE;
    private DataCache<T> cache;
}
