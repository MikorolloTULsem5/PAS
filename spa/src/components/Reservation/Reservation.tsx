import React, {useState} from "react";
import {ReservationType} from "../../types/ReservationsTypes";
import {reservationsApi} from "../../api/reservationsApi";
import ConfirmModal from "../Modal/ConfirmModal";

interface ReservationProps {
    reservation: ReservationType
}

function Reservation({reservation}: ReservationProps) {

    const [reservationCopy,setReservationCopy] = useState(reservation);

    const returnCourt = async () => {
        reservationsApi.returnCourt(reservation.court.id).then((response)=>{
            reservationsApi.getReservationsById(reservationCopy.id).then( (response) => setReservationCopy(response.data))
                .catch(console.log);
        }).catch(console.log);
    }

    return (
        <tr>
            <td>{reservationCopy.id}</td>
            <td>{reservationCopy.court.courtNumber}</td>
            <td>{reservationCopy.client.login}</td>
            <td>{reservationCopy.beginTime}</td>
            <td>{reservationCopy.endTime}</td>
            <td>{reservationCopy.reservationCost}</td>
            <td>{reservationCopy.reservationHours}</td>
            {reservationCopy.endTime!==null && <td></td>}
            {reservationCopy.endTime===null &&  <td><ConfirmModal variant={"primary"} title={'Return court'}
                                                          body={<h2>Are you sure you want to return court: {reservation.court.courtNumber} ?</h2>}
                                                          onConfirm={returnCourt}>End reservation</ConfirmModal></td>}
        </tr>
    )
}

export default Reservation;