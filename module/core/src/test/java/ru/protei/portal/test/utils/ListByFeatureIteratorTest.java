package ru.protei.portal.test.utils;

import org.junit.Assert;
import org.junit.Test;
import ru.protei.portal.tools.ListByFeatureIterator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ListByFeatureIteratorTest {
    static class Info {
        String info;
        int feature;

        public Info(String info, int feature) {
            this.info = info;
            this.feature = feature;
        }
    }

    @Test
    public void testEmpty() {
        Iterator<List<Info>> iteratorEmpty = new ListByFeatureIterator<>(
                () -> new ArrayList<>(),
                info -> info.feature);

        Assert.assertFalse(iteratorEmpty.hasNext());
    }

    @Test
    public void testTwoChunks() {
        List<Info> infos = Arrays.asList(
                new Info("chunk1_1", 1),
                new Info("chunk1_11", 1),
                new Info("chunk1_2", 2),
                new Info("chunk1_3", 3),
                new Info("chunk1_4", 4),

                new Info("chunk2_44", 4),
                new Info("chunk2_444", 4)
        );

        List<Integer> count = new ArrayList<>();
        int limit = 5;

        Iterator<List<Info>> iterator2chunks = new ListByFeatureIterator<>(
                () -> {
                    final int fromIndex = count.size() * limit;
                    final int toIndex = Math.min(infos.size(), (count.size() + 1) * limit);
                    if (fromIndex > toIndex) return new ArrayList<>();
                    final List<Info> infos1 = infos.subList(fromIndex, toIndex);
                    count.add(1);
                    return infos1;
                },
                info -> info.feature);

        List<Info> infoList;
        Assert.assertTrue(iterator2chunks.hasNext());
        infoList = iterator2chunks.next();
        Assert.assertEquals(2, infoList.size());
        Assert.assertEquals("chunk1_1", infoList.get(0).info);
        Assert.assertEquals("chunk1_11", infoList.get(1).info);
        Assert.assertEquals(1, infoList.get(0).feature);

        Assert.assertTrue(iterator2chunks.hasNext());
        infoList = iterator2chunks.next();
        Assert.assertEquals(1, infoList.size());
        Assert.assertEquals("chunk1_2", infoList.get(0).info);
        Assert.assertEquals(2, infoList.get(0).feature);

        Assert.assertTrue(iterator2chunks.hasNext());
        infoList = iterator2chunks.next();
        Assert.assertEquals(1, infoList.size());
        Assert.assertEquals("chunk1_3", infoList.get(0).info);
        Assert.assertEquals(3, infoList.get(0).feature);

        Assert.assertTrue(iterator2chunks.hasNext());
        infoList = iterator2chunks.next();
        Assert.assertEquals(3, infoList.size());
        Assert.assertEquals("chunk1_4", infoList.get(0).info);
        Assert.assertEquals("chunk2_44", infoList.get(1).info);
        Assert.assertEquals("chunk2_444", infoList.get(2).info);
        Assert.assertEquals(4, infoList.get(0).feature);

        Assert.assertFalse(iterator2chunks.hasNext());
    }
}
