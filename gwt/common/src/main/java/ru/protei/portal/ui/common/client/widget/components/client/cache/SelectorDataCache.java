package ru.protei.portal.ui.common.client.widget.components.client.cache;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.brainworm.factory.widget.table.client.InfiniteLoadHandler;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.ui.common.client.util.DataCache;
import ru.protei.portal.ui.common.client.widget.components.client.selector.LoadingHandler;

import java.util.List;

/**
 * Кеш для асинхронных селекторов
 * Запрос данных по необходимости чанками по сhunkSize, по умолчанию по 100 записей
 * количесвто хранимых чанков savedChunks, по умолчанию по 100 чанков
 */
public class SelectorDataCache<T> implements DataCache.DataCacheHandler<T>, InfiniteLoadHandler<T> {

    public SelectorDataCache() {
        cache = new DataCache<T>( this );
        cache.setChunkSize( CrmConstants.DEFAULT_SELECTOR_CHUNK_SIZE );
        cache.setSavedChunks( CrmConstants.DEFAULT_SELECTOR_SAVED_CHUNKS );
    }

    @Override
    public void onDataCacheChanged() {
        if (loadingHandler != null) loadingHandler.onLoadingComplete();
    }

    public void setSavedChunks( int savedChunks ) {
        cache.setSavedChunks( savedChunks );
    }

    public void setChunkSize( int chunkSize ) {
        cache.setChunkSize( chunkSize );
    }

    public void setLoadHandler( SelectorDataCacheLoadHandler<T> loadHandler ) {
        this.loadHandler = loadHandler;
        cache.setLoadHandler( this );
    }

    /**
     * @deprecated Для автоматического определения окончания запросов следует использовать {@link SelectorDataCache#setLoadHandler(SelectorDataCacheLoadHandler)}
     * определяется по достижению индекса записи равной общему количеству записей "total"
     */
    @Deprecated
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

    public void clearCache() {
        cache.clearCache();
        total = Integer.MAX_VALUE;
    }

    private LoadingHandler loadingHandler;
    private int total = Integer.MAX_VALUE;
    private DataCache<T> cache;

    //
    @Override
    public void loadData( int offset, int limit, AsyncCallback<List<T>> asyncCallback ) {
        loadHandler.loadData( offset, limit, new AsyncCallback<List<T>>() {
            @Override
            public void onFailure( Throwable caught ) {
                asyncCallback.onFailure( caught );
            }

            @Override
            public void onSuccess( List<T> options ) {
                if (options.size() < limit) setTotal( offset + options.size() );
                asyncCallback.onSuccess( options );
            }
        } );
    }

    private SelectorDataCacheLoadHandler<T> loadHandler;
}
