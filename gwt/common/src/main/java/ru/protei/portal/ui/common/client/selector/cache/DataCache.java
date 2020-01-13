
package ru.protei.portal.ui.common.client.selector.cache;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.brainworm.factory.widget.table.client.InfiniteLoadHandler;

import java.util.*;
import java.util.logging.Logger;

/**
 * Кеш с предзагрузкой, с храниением ограниченного числа чанков
 * Запрос данных по необходимости чанками по ChunkSize, по умолчанию по 100 записей
 */
public class DataCache<T>  {

    public interface DataCacheHandler<T> {
        void onDataCacheChanged();
    }

    public DataCache( DataCacheHandler<T> dataCacheHandler) {
        this.dataCacheHandler = dataCacheHandler;
    }

    public void setLoadHandler( InfiniteLoadHandler<T> loadHandler ) {
        this.loadHandler = loadHandler;
    }

    public void clearCache() {
        cachedData.clear();
        cachedChunks.clear();
        cachedFrom = 0;
    }

    /**
     * Количество элементов кеша хранится и запрашивается c сервера кратно ChunkSize
     * По умолчанию равен 100 (из расчета 30-ти строк на страницу)
     * Рекомендуемый размер должен привышать число строк на странице в три раза
     * @param chunkSize
     */
    public void setChunkSize( int chunkSize ) {
        CHUNK_SIZE = chunkSize;
    }

    /**
     * Пороговое значение от начала или конца ChunkSize в строках
     * при пересечении которого производится упреждающий запрос данных для пополнения кеша
     * По умолчанию равен 0.4 от ChunkSize
     * (Если CHUNK_SIZE равен 100, то THRESHOLD по умолчанию будет равен 25)
     * @param threshold
     */
    public void setPrefetchThreshold( int threshold ) {
        THRESHOLD = threshold;
    }

    /**
     * Количество хранимых  чанков размером CHUNK_SIZE
     * по умолчанию 2 чанка
     * @param savedChunks - число хранимых чанков
     */
    public void setSavedChunks( int savedChunks ) {
        this.savedChunks = savedChunks;
    }

    public T get( int offset ) {

        T row = getRow( offset );

        if ( row == null ) {
            loadChunk( offset, " |" );
        }
        else {
            tryPreload( offset );
        }

        tryClean( offset );

        return row;
    }

    private T getRow( int offset ) {
        if ( cachedFrom <= offset && offset < (cachedFrom + cachedData.size()) ) {
            return cachedData.get( offset - cachedFrom );
        }

        int loadOffset = getLoadOffset( offset );
        if ( cachedChunks.containsKey( loadOffset ) ) {
            cachedData = cachedChunks.get( loadOffset );
            cachedFrom = loadOffset;
        }

        if ( cachedFrom <= offset && offset < (cachedFrom + cachedData.size()) ) {
            return cachedData.get( offset - cachedFrom );
        }

        return null;
    }

    private void tryPreload(int offset) {
        if ( cachedChunks.isEmpty() || requesting ) {
            return;
        }

        int loadOffset = getLoadOffset(offset);
        int delta = offset - loadOffset;
        if ( (CHUNK_SIZE - delta) < THRESHOLD ) {
            if (!cachedChunks.containsKey(loadOffset+CHUNK_SIZE)) {
                loadChunk(loadOffset+CHUNK_SIZE, " |>");
            }
        }else if( delta < THRESHOLD && delta > 0){
            if ( !cachedChunks.containsKey( loadOffset - CHUNK_SIZE ) && (loadOffset - CHUNK_SIZE) > 0 ) {
                loadChunk(loadOffset-CHUNK_SIZE, " <|");
            }
        }
    }

    private void tryClean(int offset) {
        if (cachedChunks.size() <= savedChunks) return;

        Iterator<Integer> it = cachedChunks.keySet().iterator();
        while (it.hasNext()) {
            Integer key = it.next();
            if ( (key - CHUNK_SIZE) <= offset && offset <= (key + 2 * CHUNK_SIZE) ) {
                continue;
            }
            log( "Do clean               cachedChunks: " + cachedChunks.keySet() +" remove="+ key);
            it.remove();
        }

    }

    private void loadChunk(final int offset, final String prefix ) {
        if ( requesting ) {
            return;
        }

        final int loadOffset = getLoadOffset(offset);
        log( prefix+" send                         offset=" + offset + " loadOffset="+ loadOffset );

        requesting = true;
        loadHandler.loadData(loadOffset, CHUNK_SIZE, new AsyncCallback<List<T>>() {
            @Override
            public void onFailure(Throwable throwable) {
                requesting = false;
            }

            @Override
            public void onSuccess(List<T> ts) {
                requesting = false;
                log( prefix+" Receive                      offset=" + offset + " loadOffset=" + loadOffset );

                cachedChunks.put(loadOffset, ts);

                dataCacheHandler.onDataCacheChanged();
            }
        });
    }

    private void log( String s ) {
        log.info( "dc " + s );
    }


    private int getLoadOffset( int offset ) {
        int chunk = offset / CHUNK_SIZE;
        return chunk * CHUNK_SIZE;
    }

    public static final int SAVED_CHUNKS = 2;
    private int savedChunks = SAVED_CHUNKS;
    private InfiniteLoadHandler<T> loadHandler;
    private DataCacheHandler<T> dataCacheHandler;

    private int CHUNK_SIZE = 100;
    private int THRESHOLD = CHUNK_SIZE / 4;

    private List<T> cachedData = new ArrayList<T>();
    private Map<Integer, List<T>> cachedChunks = new HashMap<Integer, List<T>>();
    private int cachedFrom = 0;
    private boolean requesting = false;

    private final static Logger log = Logger.getLogger( DataCache.class.getName() );

}


