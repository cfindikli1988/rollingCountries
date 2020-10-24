package com.cfindikli.apps.rollingCountries;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isNOTcorrect() {
        assertNotEquals(5, 2 + 2);
    }

    @Test
    public void exponentIsCorrect() {
        assertEquals(4, Math.pow(2, 2), 0);
    }

}