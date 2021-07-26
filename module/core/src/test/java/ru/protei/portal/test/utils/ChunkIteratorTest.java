package ru.protei.portal.test.utils;

import org.junit.Assert;
import org.junit.Test;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.tools.ChunkIterator;

import java.util.*;
import java.util.function.Supplier;

public class ChunkIteratorTest {
    static class Item {
        String info;

        public Item(String info) {
            this.info = info;
        }
    }

    static final int limit = 4;
    static final Supplier<Boolean> noCancel = () -> false;

    @Test
    public void testEmpty() {
        Iterator<Item> iteratorEmpty = new ChunkIterator<>(
                (offset, limit) -> Result.ok(Collections.emptyList()),
                noCancel,
                limit
        );

        Assert.assertFalse(iteratorEmpty.hasNext());
    }

    @Test
    public void testCancel() {
        List<Item> items = Arrays.asList(
                new Item("chunk1_1"),
                new Item("chunk1_2"),
                new Item("chunk1_3"),
                new Item("chunk1_4"),

                new Item("chunk2_1"),
                new Item("chunk2_2")
        );

        List<Integer> chunkCount = new ArrayList<>();

        ChunkIterator<Item> iteratorCancel = new ChunkIterator<>(
                (offset, limit) -> {
                    chunkCount.add(1);
                    return Result.ok(items);
                },
                () -> true,
                limit
        );

        Assert.assertFalse(iteratorCancel.hasNext());
        Assert.assertTrue(iteratorCancel.getStatus() == En_ResultStatus.CANCELED);
        Assert.assertEquals(0, chunkCount.size());
    }

    @Test
    public void testTwoChunks() {
        List<Item> items = Arrays.asList(
                new Item("chunk1_1"),
                new Item("chunk1_2"),
                new Item("chunk1_3"),
                new Item("chunk1_4"),

                new Item("chunk2_1"),
                new Item("chunk2_2")
        );

        List<Integer> chunkCount = new ArrayList<>();

        Iterator<Item> iterator2chunks = new ChunkIterator<>(
                (offset, limit) -> {
                    final int fromIndex = chunkCount.size() * limit;
                    final int toIndex = Math.min(items.size(), (chunkCount.size() + 1) * limit);
                    if (fromIndex > toIndex) return Result.ok(new ArrayList<>());
                    final List<Item> subItems = items.subList(fromIndex, toIndex);
                    chunkCount.add(1);
                    return Result.ok(subItems);
                },
                noCancel,
                4
        );

        Item item;
        Assert.assertTrue(iterator2chunks.hasNext());

        item = iterator2chunks.next();
        Assert.assertEquals("chunk1_1", item.info);

        Assert.assertEquals(1, chunkCount.size());

        Assert.assertTrue(iterator2chunks.hasNext());
        item = iterator2chunks.next();
        Assert.assertEquals("chunk1_2", item.info);

        Assert.assertTrue(iterator2chunks.hasNext());
        item = iterator2chunks.next();
        Assert.assertEquals("chunk1_3", item.info);

        Assert.assertTrue(iterator2chunks.hasNext());
        item = iterator2chunks.next();
        Assert.assertEquals("chunk1_4", item.info);

        Assert.assertTrue(iterator2chunks.hasNext());
        item = iterator2chunks.next();
        Assert.assertEquals("chunk2_1", item.info);

        Assert.assertEquals(2, chunkCount.size());

        Assert.assertTrue(iterator2chunks.hasNext());
        item = iterator2chunks.next();
        Assert.assertEquals("chunk2_2", item.info);

        Assert.assertFalse(iterator2chunks.hasNext());
    }
}
