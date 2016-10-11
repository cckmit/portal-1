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

            invokeRebuildEvent();
        }
    }

    private void invokeRebuildEvent() {
        if (this.rebuildEventHandler != null) {
            this.rebuildEventHandler.onCacheRebuild(this.entries);
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

    public void putIfNotExists (T item) {
        if (entries.putIfAbsent(dao.getIdValue(item), item) == null)
            invokeRebuildEvent();
    }

    public void remove (T item) {
        if (entries.remove(dao.getIdValue(item)) != null)
            invokeRebuildEvent();
    }

    public void remove (Long key) {
        if (entries.remove(key) != null)
            invokeRebuildEvent();
    }

    public List<T> all () {
        return collectAll(new ArrayList<T>(entries.size()));
    }

    public boolean exists (EntitySelector<T> selector) {
        return findFirst(selector) != null;
    }

    public T findFirst (EntitySelector<T> selector) {
        ensureCacheIsNotExpired();
        for (T t : entries.values())
            if (selector.matches(t))
                return t;

        return null;
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


    public void walkThrough (Visitor<T> visitor) {
        ensureCacheIsNotExpired();
        long idx = 0;
        for (T t : entries.values()) {
            visitor.visitEntry(idx, t);
            idx++;
        }
    }

    public interface OnRebuild<T> {
        void onCacheRebuild (Map<Long, T> map);
    }

    public interface Visitor<T> {
        void visitEntry (long idx, T t);
    }

//    public static <A> EntityCache<A> create (PortalBaseDAO<A> dao, TimeUnit cacheTime) {
//        return new EntityCache<A>(dao, cacheTime);
//    }
}
