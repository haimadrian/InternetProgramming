package org.hit.internetprogramming.eoh.common.mat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test {@link Index} class, to make sure cache is working properly and indices are comparable.
 * @author Haim Adrian
 * @since 10-Jul-21
 */
public class IndexTest {
    @Test
    public void testIndexCache_clearStrongReference_referenceShouldBeRemovedFromCache() throws InterruptedException {
        @SuppressWarnings("unused")
        Index index = Index.from(0, 0);
        Index.from(0, 1);
        Index.from(1, 0);

        Assertions.assertEquals(3, Index.indicesCache.size(), "We have created three different indices");

        System.gc();
        Thread.sleep(4000);

        Assertions.assertEquals(1, Index.indicesCache.size(), "One unreferenced index supposed to be freed");
    }

    @Test
    public void testIndexComparison_compareHashCodes_shouldBeTheSame() {
        int hashCode = Index.from(0, 0).hashCode();
        int hashCode2 = Index.from(0, 0).hashCode();

        Assertions.assertEquals(hashCode, hashCode2, "HashCode of the same index was different");
    }

    @Test
    public void testIndexComparison_compareUsingEqualsMethod_shouldBeTheSame() {
        Index index = Index.from(0, 0);
        Index index2 = Index.from(0, 0);
        Index index3 = Index.from(1, 0);

        Assertions.assertEquals(index, index2, "Supposed to receive the same index");
        Assertions.assertNotEquals(index, index3, "Supposed to receive another index");
    }

    @Test
    public void testIndexComparison_compareUsingEqualsSign_shouldBeTheSame() {
        Index index = Index.from(0, 0);
        Index index2 = Index.from(0, 0);
        Index index3 = Index.from(1, 0);

        Assertions.assertSame(index, index2, "Supposed to receive the same reference");
        Assertions.assertNotSame(index, index3, "Supposed to receive another reference");
    }
}
