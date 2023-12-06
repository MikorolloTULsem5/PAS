package unittests.basicClassesTests;

import nbd.gV.exceptions.ConstructorParameterException;
import nbd.gV.model.courts.Court;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CourtTest {

    @Test
    void testCreatingCourt() {
        Court newCourt1 = new Court(null,300, 100, 1);
        assertNotNull(newCourt1);

        assertEquals(300, newCourt1.getArea());
        assertEquals(100, newCourt1.getBaseCost());
        assertEquals(1, newCourt1.getCourtNumber());
        assertFalse(newCourt1.isArchive());
        assertFalse(newCourt1.isRented());

        assertThrows(ConstructorParameterException.class, () -> new Court(null,0, 100, 99));
        assertThrows(ConstructorParameterException.class, () -> new Court(null,1, -100, 99));
        assertThrows(ConstructorParameterException.class, () -> new Court(null,1, 100, 0));
    }

    @Test
    void testSetters() {
        Court newCourt = new Court(null,300, 100, 1);
        assertNotNull(newCourt);

        assertEquals(300, newCourt.getArea());
        newCourt.setArea(20);
        assertEquals(20, newCourt.getArea());

        assertEquals(100, newCourt.getBaseCost());
        newCourt.setBaseCost(10);
        assertEquals(10, newCourt.getBaseCost());

        assertFalse(newCourt.isArchive());
        newCourt.setArchive(true);
        assertTrue(newCourt.isArchive());
        newCourt.setArchive(false);
        assertFalse(newCourt.isArchive());

        assertFalse(newCourt.isRented());
        newCourt.setRented(true);
        assertTrue(newCourt.isRented());
        newCourt.setRented(false);
        assertFalse(newCourt.isRented());
    }
}
