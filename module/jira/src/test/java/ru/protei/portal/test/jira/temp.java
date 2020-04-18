package ru.protei.portal.test.jira;

import org.junit.Test;
import ru.protei.portal.core.model.helper.JiraMarkUpUtils;
import ru.protei.portal.core.utils.JiraUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.protei.portal.core.utils.JiraUtils.parseImageNode;

public class temp {

    @Test
    public void testRegexp(){
        String extLink = "20200416/11777";
        String str = "картинке !19_1020_duck.png|alt=atl_name! еее !not_duck.jpeg!";
        Pattern p = Pattern.compile("\\![^\\!]*[\\!]");
        Matcher m = p.matcher(str);
        while (m.find()) {
            String group = m.group();
            JiraUtils.ImageNode imageNode = parseImageNode(group.substring(1, group.length() - 1));
            String imageString = JiraMarkUpUtils.makeImageString(extLink, imageNode.link);
            System.out.println( imageString );
        }
    }
}
