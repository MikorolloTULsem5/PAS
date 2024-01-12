package unittests.mappersTests;

import pas.gV.model.logic.courts.Court;
import pas.gV.model.data.datahandling.entities.CourtEntity;
import pas.gV.model.data.datahandling.mappers.CourtMapper;
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
        CourtEntity courtMapper = new CourtEntity(uuid.toString(), 300, 200, 1,
                false, 1);
        assertNotNull(courtMapper);

        assertEquals(uuid, UUID.fromString(courtMapper.getId()));
        assertEquals(300, courtMapper.getArea());
        assertEquals(200, courtMapper.getBaseCost());
        assertEquals(1, courtMapper.getCourtNumber());
        assertFalse(courtMapper.isArchive());
        assertTrue(courtMapper.isRented() > 0);
    }

    @Test
    void testToMongoClientMethod() {
        Court court = new Court(UUID.randomUUID(),300, 100, 1);
        assertNotNull(court);

        CourtEntity courtMapper = CourtMapper.toMongoCourt(court);
        assertNotNull(courtMapper);

        assertEquals(court.getId(), UUID.fromString(courtMapper.getId()));
        assertEquals(court.getArea(), courtMapper.getArea());
        assertEquals(court.getBaseCost(), courtMapper.getBaseCost());
        assertEquals(court.getCourtNumber(), courtMapper.getCourtNumber());
        assertFalse(courtMapper.isArchive());
        assertFalse(courtMapper.isRented() > 0);
    }

    @Test
    void testFromMongoClientMethod() {
        CourtEntity courtMapper = new CourtEntity(uuid.toString(), 300, 200, 1,
                true, 0);
        assertNotNull(courtMapper);

        Court court = CourtMapper.fromMongoCourt(courtMapper);
        assertNotNull(court);

        assertEquals(UUID.fromString(courtMapper.getId()), court.getId());
        assertEquals(courtMapper.getArea(), court.getArea());
        assertEquals(courtMapper.getBaseCost(), court.getBaseCost());
        assertEquals(courtMapper.getCourtNumber(), court.getCourtNumber());
        assertTrue(court.isArchive());
        assertFalse(court.isRented());
    }
}
