package ru.protei.portal.test.jira;

import org.junit.Test;
import ru.protei.portal.core.model.helper.MarkDownUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class temp {
    @Test
    public void testRegexp(){
        String str = "картинке !19_1020_duck.png! еее !not_duck.jpeg!";
        Pattern p = Pattern.compile("\\![^\\!]*\\!");
        Matcher m = p.matcher(str);
        while (m.find()) {
            String group = m.group();
            String imageString = MarkDownUtils.makeImageString("alt", "path/" + group.substring(1, group.length() - 1));
            System.out.println( imageString );
        }
    }
}
