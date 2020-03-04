package ru.protei.portal.redmine.utils;

@FunctionalInterface
public interface FourConsumer<A, B, C, D, E> {
    void apply(A a, B b, C c, D d, E e);
}
