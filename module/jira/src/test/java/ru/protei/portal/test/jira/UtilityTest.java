package ru.protei.portal.test.jira;

import org.junit.Assert;
import org.junit.Test;
import ru.protei.portal.jira.service.IssueMergeState;

public class UtilityTest {

    @Test
    public void testIssueStateParse () {
        IssueMergeState state = IssueMergeState.fromJSON("{\"cid\":[1,2,3]}");

        Assert.assertEquals(3, state.commentsCount());
        Assert.assertEquals(0, state.attachmentsCount());

        state = IssueMergeState.fromJSON("{\"cid\":[55], \"aid\" : [4,5]}");

        Assert.assertEquals(1, state.commentsCount());
        Assert.assertEquals(2, state.attachmentsCount());
    }


    @Test
    public void testIssuePack () {
        IssueMergeState mergeState = new IssueMergeState();
        mergeState.appendComment(1)
                .appendComment(2)
                .appendComment(3)
                .appendAttachment(4)
                .appendAttachment(4)
                .appendAttachment(7);

        System.out.println(mergeState.toString());

        Assert.assertEquals("{\"cid\":[1,2,3],\"aid\":[4,7]}", mergeState.toString());

    }
}
