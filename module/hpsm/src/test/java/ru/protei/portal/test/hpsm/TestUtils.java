package ru.protei.portal.test.hpsm;

import org.junit.Assert;
import org.junit.Test;
import ru.protei.portal.hpsm.api.HpsmStatus;
import ru.protei.portal.hpsm.struct.HpsmMessageHeader;
import ru.protei.portal.hpsm.utils.HpsmUtils;

import java.text.ParseException;
import java.util.Date;

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


    @Test
    public void testParseMessage001 () {

        HpsmMessageHeader testHeader = HpsmMessageHeader.parse("*ТЕСТ*ID_HPSM=[RTS1631]ID_VENDOR=[]STATUS=[Новый]");
        Assert.assertNotNull(testHeader);
        Assert.assertEquals("RTS1631", testHeader.getHpsmId());
        Assert.assertTrue(testHeader.isNewCaseRequest());

        Assert.assertEquals(HpsmStatus.NEW, testHeader.getStatus());

    }


    @Test
    public void testDateParsing001 () throws ParseException{
        Date oldParsed = HpsmUtils.parseDate("25/05/2017 10:25:49");
        Date newParsed = HpsmUtils.parseDate("2017-05-25T10:25:49+03:00");

        Assert.assertNotNull(oldParsed);
        Assert.assertNotNull(newParsed);

        Assert.assertEquals(oldParsed,newParsed);

//        System.out.println(HpsmUtils.formatDate(new Date()));
    }
}
