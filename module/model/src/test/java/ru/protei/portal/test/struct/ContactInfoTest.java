package ru.protei.portal.test.struct;

import org.junit.Assert;
import org.junit.Test;
import ru.protei.portal.core.model.struct.ContactInfo;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;

/**
 * Created by Mike on 12.11.2016.
 */
public class ContactInfoTest {

    @Test
    public void testPlainFacade_empty () {

        PlainContactInfoFacade facade = new PlainContactInfoFacade(new ContactInfo());

        Assert.assertNull(facade.getEmail());
        Assert.assertNull(facade.getEmail_own());
        Assert.assertNull(facade.getWorkPhone());

    }
}
