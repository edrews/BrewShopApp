package com.brew.brewshop.util;

import junit.framework.Assert;
import junit.framework.TestCase;

public class UtilTest extends TestCase {
    private static final String TAG = UtilTest.class.getName();

    @Override
    public void setUp() {

    }

    public void testGetTinsethIbu() {
        double ibu;

        ibu = Util.getTinsethIbu(60, 1, 5.0, 5.0, 1.050);
        Assert.assertEquals(17.3, ibu, 0.05);

        ibu = Util.getTinsethIbu(60, 1, 5.0, 5.0, 1.100);
        Assert.assertEquals(11, ibu, 0.05);

        ibu = Util.getTinsethIbu(30, 1, 5.0, 5.0, 1.050);
        Assert.assertEquals(13.3, ibu, 0.05);

        ibu = Util.getTinsethIbu(10, 1, 5.0, 5.0, 1.050);
        Assert.assertEquals(6.3, ibu, 0.05);
    }
}
