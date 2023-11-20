package mappersTests;

import nbd.gV.courts.Court;
import nbd.gV.mappers.CourtMapper;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CourtMapperTest {
    UUID uuid = UUID.randomUUID();
    @Test
    void testCreatingMapper() {
        CourtMapper courtMapper = new CourtMapper(uuid.toString(), 300, 200, 1,
                false, 1);
        assertNotNull(courtMapper);

        assertEquals(uuid, UUID.fromString(courtMapper.getCourtId()));
        assertEquals(300, courtMapper.getArea());
        assertEquals(200, courtMapper.getBaseCost());
        assertEquals(1, courtMapper.getCourtNumber());
        assertFalse(courtMapper.isArchive());
        assertTrue(courtMapper.isRented() > 0);
    }

    @Test
    void testToMongoClientMethod() {
        Court court = new Court(300, 100, 1);
        assertNotNull(court);

        CourtMapper courtMapper = CourtMapper.toMongoCourt(court);
        assertNotNull(courtMapper);

        assertEquals(court.getCourtId(), UUID.fromString(courtMapper.getCourtId()));
        assertEquals(court.getArea(), courtMapper.getArea());
        assertEquals(court.getBaseCost(), courtMapper.getBaseCost());
        assertEquals(court.getCourtNumber(), courtMapper.getCourtNumber());
        assertFalse(courtMapper.isArchive());
        assertFalse(courtMapper.isRented() > 0);
    }

    @Test
    void testFromMongoClientMethod() {
        CourtMapper courtMapper = new CourtMapper(uuid.toString(), 300, 200, 1,
                true, 0);
        assertNotNull(courtMapper);

        Court court = CourtMapper.fromMongoCourt(courtMapper);
        assertNotNull(court);

        assertEquals(UUID.fromString(courtMapper.getCourtId()), court.getCourtId());
        assertEquals(courtMapper.getArea(), court.getArea());
        assertEquals(courtMapper.getBaseCost(), court.getBaseCost());
        assertEquals(courtMapper.getCourtNumber(), court.getCourtNumber());
        assertTrue(court.isArchive());
        assertFalse(court.isRented());
    }
}
