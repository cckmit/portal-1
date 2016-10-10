package ru.protei.portal.core.utils;

import ru.protei.portal.core.model.dao.PortalBaseDAO;
import ru.protei.winter.core.utils.collections.Converter;

import java.util.*;

/**
 * Created by michael on 10.10.16.
 */
public class EntityCache<T> {

    Map<Long, T> entries;
    PortalBaseDAO<T> dao;
    long cacheTime;
    long lastReadTime;

    OnRebuild<T> rebuildEventHandler;

    public EntityCache (PortalBaseDAO<T> dao, long cacheTime) {
        this (dao, cacheTime, null);
    }

    public EntityCache (PortalBaseDAO<T> dao, long cacheTime, OnRebuild<T> rebuildEventHandler) {
        this.cacheTime = cacheTime;
        this.dao = dao;
        this.rebuildEventHandler = rebuildEventHandler;

        rebuildCache();
    }

    public void destroy () {
        this.dao = null;
        if (this.entries != null)
            this.entries.clear();
        this.entries = null;
    }

    private void rebuildCache() {
        synchronized (this) {
            if (this.entries != null) {
                this.entries.clear();
            }
            else {
                this.entries = new HashMap<Long, T>();
            }

            for (T t : dao.getAll()) {
                this.entries.put(dao.getIdValue(t), t);
            }

            this.lastReadTime = System.currentTimeMillis();

            if (this.rebuildEventHandler != null) {
                this.rebuildEventHandler.onCacheRebuild(this.entries);
            }
        }
    }

    private void ensureCacheIsNotExpired () {
        if (cacheTime  + lastReadTime < System.currentTimeMillis())
            rebuildCache();
    }

    public T get (Long key) {
        ensureCacheIsNotExpired();
        return entries.get(key);
    }

    public List<T> collectAll (List<T> list) {
        ensureCacheIsNotExpired();
        list.addAll(entries.values());
        return list;
    }

    public List<T> all () {
        return collectAll(new ArrayList<T>(entries.size()));
    }

    public <R> List<R> collectAndConvert (EntitySelector<T> selector, List<R> list, Converter<T,R> converter) {
        ensureCacheIsNotExpired();
        for (T t : entries.values()) {
            if (selector.matches(t))
                list.add(converter.convert(t));
        }
        return list;
    }


    public List<T> collect (EntitySelector<T> selector, List<T> list) {
        ensureCacheIsNotExpired();
        for (T t : entries.values()) {
            if (selector.matches(t))
                list.add(t);
        }
        return list;
    }

    public Collection<T> filter (EntitySelector<T> selector) {
        return collect(selector, new ArrayList<T>());
    }


    public interface OnRebuild<T> {
        void onCacheRebuild (Map<Long, T> map);
    }


//    public static <A> EntityCache<A> create (PortalBaseDAO<A> dao, TimeUnit cacheTime) {
//        return new EntityCache<A>(dao, cacheTime);
//    }
}
