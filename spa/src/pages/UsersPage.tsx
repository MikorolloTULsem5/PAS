import {Table} from "react-bootstrap";
import {useEffect, useState} from "react";
import {UserType} from "../types/Users";
import {adminsApi} from "../api/adminsApi";
import {resAdminsApi} from "../api/resAdminsApi";
import {clientsApi} from "../api/clientsApi";
import User from "../components/User";

function UsersPage() {
    const [users, setUsers] = useState<UserType[]>([])
    const getUsers = async () => {
        let usersCopy:UserType[] = []
        try {
            let {data} = await adminsApi.getAdmins();
            data.forEach((user)=>{user.userType = "Admin"})
            usersCopy = [...usersCopy,...data]
        } catch (error) {
            console.log({error})
        }

        try {
            let {data} = await resAdminsApi.getResAdmins();
            data.forEach((user)=>{user.userType = "Resource admin"})
            usersCopy = [...usersCopy,...data]
        } catch (error) {
            console.log({error})
        }

        try {
            let {data} = await clientsApi.getClients();
            data.forEach((user)=>{user.userType = "Client"})
            usersCopy = [...usersCopy,...data]
        } catch (error) {
            console.log({error})
        }

        setUsers(usersCopy);
    }

    useEffect(() => {
        getUsers();
    }, []);

    return (
        <div>
            <Table striped hover>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Login</th>
                    <th>Account type</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {users.map((user)=> (
                    <User key={user.id} user={user} users={users} setUsers={setUsers}/>
                ))}
                </tbody>
            </Table>
        </div>
    )
}

export default UsersPage