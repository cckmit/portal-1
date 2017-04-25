package ru.protei.portal.test.hpsm;

import org.junit.Assert;
import org.junit.Test;
import ru.protei.portal.hpsm.HpsmUtils;

/**
 * Created by michael on 25.04.17.
 */
public class TestUtils {

    @Test
    public void testOptionExtr001 () {

        String testData = "prefix;OPTION=[PASS]; TIME=[PASS]; VALUE=[111]; BAD_OPT1=QQQ; BAD_OPT2=QQQ]; BAD_OPT3=[QQQ";

        Assert.assertEquals("PASS", HpsmUtils.extractOption(testData, "OPTION", "FAIL"));
        Assert.assertEquals("PASS", HpsmUtils.extractOption(testData, "TIME", "FAIL"));
        Assert.assertEquals("111", HpsmUtils.extractOption(testData, "VALUE", "FAIL"));

        // test invalid options, expect fail as normal result
        Assert.assertEquals("FAIL", HpsmUtils.extractOption(testData, "BAD_OPT1", "FAIL"));
        Assert.assertEquals("FAIL", HpsmUtils.extractOption(testData, "BAD_OPT2", "FAIL"));
        Assert.assertEquals("FAIL", HpsmUtils.extractOption(testData, "BAD_OPT3", "FAIL"));
    }
}
