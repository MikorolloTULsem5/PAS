import {Table} from "react-bootstrap";
import {useEffect, useState} from "react";
import {ReservationType} from "../types/ReservationsTypes";
import Reservation from "../components/Reservation/Reservation";
import {reservationsApi} from "../api/reservationsApi";
import {useAccount} from "../hooks/useAccount";
import {AccountTypeEnum} from "../types/Users";
import {clientsApi} from "../api/clientsApi";

function ReservationsPage() {
    const [reservations, setReservations] = useState<ReservationType[]>([]);
    const {accountType, getCurrentAccount} = useAccount();
    useEffect(() => {
        getCurrentAccount();
    }, []);


    useEffect(() => {
        if(accountType===AccountTypeEnum.CLIENT){
            reservationsApi.getClientReservations().then(async (response) => {
                setReservations((await response).data)
            })
        }else{
            reservationsApi.getAllReservations().then((result)=>setReservations(result))
        }
    }, []);

    return (
        <Table striped hover>
            <thead>
            <tr>
                <th>ID</th>
                <th>Court number</th>
                <th>User login</th>
                <th>Begin time</th>
                <th>End time</th>
                <th>Cost</th>
                <th>Total time</th>
                <th>Actions</th>
            </tr>
            </thead>
            <tbody>
            {reservations.length!==0 && reservations.map((reservation) => (
                    <Reservation key={reservation.id} reservation={reservation}></Reservation>
                )
            )}
            </tbody>
        </Table>
    )
}

export default ReservationsPage