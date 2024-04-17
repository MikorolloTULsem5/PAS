import {Table} from "react-bootstrap";
import {useEffect, useState} from "react";
import {ReservationType} from "../types/ReservationsTypes";
import Reservation from "../components/Reservation/Reservation";
import {reservationsApi} from "../api/reservationsApi";
import {useAccount} from "../hooks/useAccount";
import {AccountTypeEnum} from "../types/Users";

function ReservationsPage() {
    const [reservations, setReservations] = useState<ReservationType[]>([]);
    const [trigger, setTrigger] = useState(false);
    const {account, getCurrentAccount} = useAccount();

    useEffect(() => {
        getCurrentAccount();
        if(account?.userType === AccountTypeEnum.CLIENT){
            reservationsApi.getClientReservations().then((result)=>setReservations( (result).data)).catch(console.log)
        }else{
            reservationsApi.getAllReservations().then((result)=>setReservations(result)).catch(console.log)
        }
    }, [trigger]);

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
                    <Reservation key={reservation.id} reservation={reservation} trigger={trigger} setTrigger={setTrigger}
                    accountType={account?.userType}></Reservation>
                )
            )}
            </tbody>
        </Table>
    )
}

export default ReservationsPage