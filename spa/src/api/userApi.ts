import {clientsApi} from "./clientsApi";
import {adminsApi} from "./adminsApi";
import {resAdminsApi} from "./resAdminsApi";
import {UserType} from "../types/Users";

export const usersApi = {
    getUsers: async (): Promise<UserType[]> => {
        let usersCopy: UserType[] = []
        try {
            let {data} = await adminsApi.getAdmins();
            usersCopy = [...usersCopy, ...data]
        } catch (error) {
            console.log({error})
        }

        try {
            let {data} = await resAdminsApi.getResAdmins();
            usersCopy = [...usersCopy, ...data]
        } catch (error) {
            console.log({error})
        }

        try {
            let {data} = await clientsApi.getClients();
            usersCopy = [...usersCopy, ...data]
        } catch (error) {
            console.log({error})
        }
        return usersCopy;
    },

    activate: (user:UserType) => {
        switch (user.userType) {
            case "Client":
                return clientsApi.activate(user.id);
            case "Admin":
                return adminsApi.activate(user.id);
            case "Resource admin":
                return resAdminsApi.activate(user.id);
        }
    },

    deactivate: (user:UserType) => {
        switch (user.userType) {
            case "Client":
                return clientsApi.deactivate(user.id);
            case "Admin":
                return adminsApi.deactivate(user.id);
            case "Resource admin":
                return resAdminsApi.deactivate(user.id);
        }
    },

    modify: (user:UserType) => {

        switch (user.userType) {
            case "Client":
                // @ts-ignore
                return clientsApi.modify(user);
            case "Admin":
                return adminsApi.modify(user);
            case "Resource admin":
                return resAdminsApi.modify(user);
        }
    }
}