import React, {Dispatch, SetStateAction, useEffect, useState} from "react";
import {ReservationType} from "../../types/ReservationsTypes";
import {reservationsApi} from "../../api/reservationsApi";
import ConfirmModal from "../Modal/ConfirmModal";
import {AccountTypeEnum} from "../../types/Users";

interface ReservationProps {
    reservation: ReservationType,
    trigger:boolean,
    setTrigger:Dispatch<SetStateAction<boolean>>,
    accountType: AccountTypeEnum | null | undefined
}

function Reservation({reservation, trigger, setTrigger,accountType}: ReservationProps) {

    const [reservationCopy,setReservationCopy] = useState(reservation);

    useEffect(() => {
        setReservationCopy(reservation)
    }, [reservation]);

    const returnCourt = async () => {
        if(accountType === AccountTypeEnum.CLIENT){
            reservationsApi.returnCourtClient(reservation.court.id).then((response)=>{
                setTrigger(!trigger);
            }).catch(console.log);
        } else {
            reservationsApi.returnCourt(reservation.court.id).then((response)=>{
                setTrigger(!trigger);
            }).catch(console.log);
        }
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