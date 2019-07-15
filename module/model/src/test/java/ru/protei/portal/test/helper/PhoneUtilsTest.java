package ru.protei.portal.test.helper;

import org.junit.Assert;
import org.junit.Test;
import ru.protei.portal.core.model.helper.PhoneUtils;

import java.util.HashMap;
import java.util.Map;

public class PhoneUtilsTest {

    @Test
    public void testNormalizePhoneNumber() {

        Map<String, String> phoneMap = new HashMap<>();
        phoneMap.put("+7 (999) 888-77-66", "+79998887766");
        phoneMap.put("+7 999 888-77-66", "+79998887766");
        phoneMap.put("+7 999 888 77 66", "+79998887766");
        phoneMap.put("+7 999 8887766", "+79998887766");
        phoneMap.put("+7(999) 8887766", "+79998887766");
        phoneMap.put("+7(999)8887766", "+79998887766");
        phoneMap.put("+79998887766", "+79998887766");
        phoneMap.put("8 (999) 888-77-66", "89998887766");
        phoneMap.put("8 999 888-77-66", "89998887766");
        phoneMap.put("8 999 888 77 66", "89998887766");
        phoneMap.put("8 999 8887766", "89998887766");
        phoneMap.put("8(999) 8887766", "89998887766");
        phoneMap.put("8(999)8887766", "89998887766");
        phoneMap.put("89998887766", "89998887766");

        phoneMap.forEach((number, expected) -> {
            Assert.assertEquals(number, expected, PhoneUtils.normalizePhoneNumber(number));
        });
    }

    @Test
    public void testPrettyPrintPhoneNumber() {

        Map<String, String> phoneMap = new HashMap<>();
        phoneMap.put("+79998887766", "+7 (999) 888-77-66");
        phoneMap.put("+7999887766", "+7 (999) 88-77-66");
        phoneMap.put("89998887766", "8 (999) 888-77-66");
        phoneMap.put("8999887766", "8 (999) 88-77-66");
        phoneMap.put("123", "123");
        phoneMap.put("+7 (999) 888-77-66", "+7 (999) 888-77-66");
        phoneMap.put("+7 999 888-77-66", "+7 999 888-77-66");
        phoneMap.put("+7(999)8887766", "+7(999)8887766");
        phoneMap.put("(809) 123-1234", "(809) 123-1234");

        phoneMap.forEach((number, expected) -> {
            Assert.assertEquals(number, expected, PhoneUtils.prettyPrintPhoneNumber(number));
        });
    }
}
