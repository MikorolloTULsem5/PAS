package unittests.mappersTests;

import pas.gV.model.users.Client;
import pas.gV.model.courts.Court;
import pas.gV.data.datahandling.dto.ClientDTO;
import pas.gV.data.datahandling.mappers.ClientMapper;
import pas.gV.data.datahandling.dto.CourtDTO;
import pas.gV.data.datahandling.dto.ReservationDTO;
import pas.gV.data.datahandling.mappers.CourtMapper;
import pas.gV.data.datahandling.mappers.ReservationMapper;
import pas.gV.model.reservations.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ReservationMapperTest {
    UUID uuid = UUID.randomUUID();
    Client testClient;
    ClientDTO testClientMapper;
    Court testCourt;
    CourtDTO testCourtMapper;

    LocalDateTime testTimeStart;
    LocalDateTime testTimeEnd;

    @BeforeEach
    void setUp() {
        testClient = new Client(UUID.randomUUID(), "John", "Smith", "12345678901", "normal");
        testClientMapper = ClientMapper.toMongoUser(testClient);

        testCourt = new Court(null, 1000, 100, 1);
        testCourtMapper = CourtMapper.toMongoCourt(testCourt);

        testTimeStart = LocalDateTime.of(2023, Month.JUNE, 4, 12, 0);
        testTimeEnd = LocalDateTime.of(2023, Month.JUNE, 4, 15, 0);
    }

    @Test
    void testCreatingMapper() {
        ReservationDTO reservationMapper = new ReservationDTO(uuid.toString(),
                testClient.getId().toString(), testCourt.getId().toString(), testTimeStart, testTimeEnd,
                200);
        assertNotNull(reservationMapper);

        assertEquals(uuid, UUID.fromString(reservationMapper.getId()));
        assertEquals(testClient.getId(), UUID.fromString(reservationMapper.getClientId()));
        assertEquals(testCourt.getId(), UUID.fromString(reservationMapper.getCourtId()));
        assertEquals(testTimeStart, reservationMapper.getBeginTime());
        assertEquals(testTimeEnd, reservationMapper.getEndTime());
        assertEquals(200, reservationMapper.getReservationCost());
    }

    @Test
    void testToMongoClientMethod() {
        Reservation reservation = new Reservation(UUID.randomUUID(), testClient, testCourt, testTimeStart);
        assertNotNull(reservation);

        ReservationDTO reservationMapper = ReservationMapper.toMongoReservation(reservation);
        assertNotNull(reservationMapper);

        assertEquals(reservation.getId(), UUID.fromString(reservationMapper.getId()));
        assertEquals(testClient.getId(), UUID.fromString(reservationMapper.getClientId()));
        assertEquals(testCourt.getId(), UUID.fromString(reservationMapper.getCourtId()));
        assertEquals(testTimeStart, reservationMapper.getBeginTime());
        assertNull(reservationMapper.getEndTime());
        assertEquals(0, reservationMapper.getReservationCost());

        Reservation reservationEnded = new Reservation(UUID.randomUUID(), testClient, testCourt, testTimeStart);
        assertNotNull(reservationEnded);
        reservationEnded.endReservation(testTimeEnd);

        ReservationDTO reservationMapperEnded = ReservationMapper.toMongoReservation(reservationEnded);
        assertNotNull(reservationMapperEnded);

        assertEquals(reservationEnded.getId(), UUID.fromString(reservationMapperEnded.getId()));
        assertEquals(testClient.getId(), UUID.fromString(reservationMapperEnded.getClientId()));
        assertEquals(testCourt.getId(), UUID.fromString(reservationMapperEnded.getCourtId()));
        assertEquals(testTimeStart, reservationMapperEnded.getBeginTime());
        assertEquals(testTimeEnd, reservationMapperEnded.getEndTime());
        assertEquals(300, reservationMapperEnded.getReservationCost());
    }

    @Test
    void testFromMongoClientMethod() {
        ReservationDTO reservationMapper = new ReservationDTO(uuid.toString(),
                testClient.getId().toString(), testCourt.getId().toString(), testTimeStart, null,
                0);
        assertNotNull(reservationMapper);

        Reservation reservation = ReservationMapper.fromMongoReservation(reservationMapper,
                ClientMapper.toMongoUser(testClient), CourtMapper.toMongoCourt(testCourt));
        assertNotNull(reservation);

        assertEquals(uuid, reservation.getId());
        assertEquals(testClient.getId(), reservation.getClient().getId());
        assertEquals(testCourt.getId(), reservation.getCourt().getId());
        assertEquals(testTimeStart, reservation.getBeginTime());
        assertNull(reservation.getEndTime());
        assertEquals(0, reservation.getReservationCost());


        ReservationDTO reservationMapperEnded = new ReservationDTO(uuid.toString(),
                testClient.getId().toString(), testCourt.getId().toString(), testTimeStart, testTimeEnd,
                300);
        assertNotNull(reservationMapperEnded);

        Reservation reservationEnded = ReservationMapper.fromMongoReservation(reservationMapperEnded,
                ClientMapper.toMongoUser(testClient), CourtMapper.toMongoCourt(testCourt));
        assertNotNull(reservationEnded);

        assertEquals(UUID.fromString(reservationMapperEnded.getId()), reservationEnded.getId());
        assertEquals(testClient.getId(), reservationEnded.getClient().getId());
        assertEquals(testCourt.getId(), reservationEnded.getCourt().getId());
        assertEquals(testTimeStart, reservationEnded.getBeginTime());
        assertEquals(testTimeEnd, reservationEnded.getEndTime());
        assertEquals(300, reservationEnded.getReservationCost());
    }
}
