package io.space.object;

public interface Filter<T> {
    boolean check(T o);
}
