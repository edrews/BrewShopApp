package com.brew.brewshop.util;

import junit.framework.Assert;
import junit.framework.TestCase;

public class UtilTest extends TestCase {
    private static final String TAG = UtilTest.class.getName();

    public void testGetHopUtilization() {
        double util;
        util = Util.calculateHopUtilization(60, 1);
        assertEquals(0.36, util, 0.005);
        util = Util.calculateHopUtilization(30, 1);
        assertEquals(0.28, util, 0.005);
        util = Util.calculateHopUtilization(10, 1);
        assertEquals(0.13, util, 0.005);
        util = Util.calculateHopUtilization(60, 1.03);
        assertEquals(0.28, util, 0.005);
        util = Util.calculateHopUtilization(60, 1.06);
        assertEquals(0.21, util, 0.005);
        util = Util.calculateHopUtilization(60, 1.09);
        assertEquals(0.16, util, 0.005);
        util = Util.calculateHopUtilization(60, 1.12);
        assertEquals(0.12, util, 0.005);
    }

    public void testGetTinsethIbu() {
        double ibu;
        ibu = Util.getTinsethIbu(60, 1, 5.0, 5.0, 1.050);
        Assert.assertEquals(17.3, ibu, 0.05);

        ibu = Util.getTinsethIbu(30, 1, 5.0, 5.0, 1.050);
        Assert.assertEquals(13.3, ibu, 0.05);

        ibu = Util.getTinsethIbu(60, 3, 5.0, 5.0, 1.050);
        Assert.assertEquals(51.8, ibu, 0.05);

        ibu = Util.getTinsethIbu(60, 1, 10.0, 5.0, 1.050);
        Assert.assertEquals(34.6, ibu, 0.05);

        ibu = Util.getTinsethIbu(60, 1, 5.0, 10.0, 1.050);
        Assert.assertEquals(8.6, ibu, 0.05);

        ibu = Util.getTinsethIbu(60, 1, 5.0, 5.0, 1.100);
        Assert.assertEquals(11.0, ibu, 0.05);
    }
}
