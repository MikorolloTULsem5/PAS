import {Table} from "react-bootstrap";
import {useEffect, useState} from "react";
import {UserType} from "../types/Users";
import User from "../components/User";
import {usersApi} from "../api/userApi";

function UsersPage() {
    const [users, setUsers] = useState<UserType[]>([])

    useEffect(() => {
        usersApi.getUsers().then((getUsers) => setUsers(getUsers));
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