package io.space.utils;

import java.util.LinkedList;
import java.util.function.Consumer;

public final class ListsUtils {
    public static <T> void foreachWithPoll(LinkedList<T> linkedList, Consumer<T> consumer) {
        T o;

        while ((o = linkedList.poll()) != null) {
            consumer.accept(o);
        }
    }
}
