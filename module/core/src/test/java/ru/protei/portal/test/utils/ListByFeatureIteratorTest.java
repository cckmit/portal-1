package ru.protei.portal.test.utils;

import org.junit.Assert;
import org.junit.Test;
import ru.protei.portal.tools.ListByFeatureIterator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ListByFeatureIteratorTest {
    static class Item {
        String info;
        int feature;

        public Item(String info, int feature) {
            this.info = info;
            this.feature = feature;
        }
    }

    @Test
    public void testEmpty() {
        Iterator<List<Item>> iteratorEmpty = new ListByFeatureIterator<>(
                () -> new ArrayList<>(),
                info -> info.feature);

        Assert.assertFalse(iteratorEmpty.hasNext());
    }

    @Test
    public void testTwoChunks() {
        List<Item> items = Arrays.asList(
                new Item("chunk1_1", 1),
                new Item("chunk1_11", 1),
                new Item("chunk1_2", 2),
                new Item("chunk1_3", 3),
                new Item("chunk1_4", 4),

                new Item("chunk2_44", 4),
                new Item("chunk2_444", 4)
        );

        List<Integer> count = new ArrayList<>();
        int limit = 5;

        Iterator<List<Item>> iterator2chunks = new ListByFeatureIterator<>(
                () -> {
                    final int fromIndex = count.size() * limit;
                    final int toIndex = Math.min(items.size(), (count.size() + 1) * limit);
                    if (fromIndex > toIndex) return new ArrayList<>();
                    final List<Item> subItems = items.subList(fromIndex, toIndex);
                    count.add(1);
                    return subItems;
                },
                info -> info.feature);

        List<Item> itemList;
        Assert.assertTrue(iterator2chunks.hasNext());
        itemList = iterator2chunks.next();
        Assert.assertEquals(2, itemList.size());
        Assert.assertEquals("chunk1_1", itemList.get(0).info);
        Assert.assertEquals("chunk1_11", itemList.get(1).info);
        Assert.assertEquals(1, itemList.get(0).feature);

        Assert.assertTrue(iterator2chunks.hasNext());
        itemList = iterator2chunks.next();
        Assert.assertEquals(1, itemList.size());
        Assert.assertEquals("chunk1_2", itemList.get(0).info);
        Assert.assertEquals(2, itemList.get(0).feature);

        Assert.assertTrue(iterator2chunks.hasNext());
        itemList = iterator2chunks.next();
        Assert.assertEquals(1, itemList.size());
        Assert.assertEquals("chunk1_3", itemList.get(0).info);
        Assert.assertEquals(3, itemList.get(0).feature);

        Assert.assertTrue(iterator2chunks.hasNext());
        itemList = iterator2chunks.next();
        Assert.assertEquals(3, itemList.size());
        Assert.assertEquals("chunk1_4", itemList.get(0).info);
        Assert.assertEquals("chunk2_44", itemList.get(1).info);
        Assert.assertEquals("chunk2_444", itemList.get(2).info);
        Assert.assertEquals(4, itemList.get(0).feature);

        Assert.assertFalse(iterator2chunks.hasNext());
    }
}
