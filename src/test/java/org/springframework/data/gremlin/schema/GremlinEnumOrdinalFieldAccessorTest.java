package org.springframework.data.gremlin.schema;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.gremlin.schema.property.accessor.GremlinEnumOrdinalFieldPropertyAccessor;
import org.springframework.data.gremlin.schema.property.accessor.GremlinPropertyAccessor;

import static org.junit.Assert.assertEquals;

/**
 * Created by gman on 18/05/15.
 */
public class GremlinEnumOrdinalFieldAccessorTest {
    TestObject obj1;

    GremlinPropertyAccessor accessor;

    @Before
    public void setUp() throws Exception {
        obj1 = new TestObject();

        accessor = new GremlinEnumOrdinalFieldPropertyAccessor(TestObject.class.getDeclaredField("test"), TestObject.TEST.class);
    }


    @Test
    public void should_read_all_variables() throws Exception {
        assertEquals(0, accessor.get(obj1));
    }

    @Test
    public void should_read_null_variables() throws Exception {
        obj1.test = null;
        assertEquals(null, accessor.get(obj1));
    }

    @Test
    public void should_write_all_variables() throws Exception {
        accessor.set(obj1, 1);
        assertEquals(obj1.test, TestObject.TEST.TWO);
    }

    @Test
    public void should_write_all_nulls() throws Exception {
        accessor.set(obj1, null);
        assertEquals(null, obj1.test);
    }

    static class TestObject {
        public enum TEST {
            ONE,
            TWO
        }

        TEST test = TEST.ONE;
    }

}