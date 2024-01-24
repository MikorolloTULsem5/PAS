import {apiWithConfig} from "./api.config";
import {ApiResponseType} from "../types/ApiResponseType";
import {ClientType, UserType} from "../types/Users";

export const clientsApi = {
    getClients: async (): Promise<ApiResponseType<ClientType[]>> => {
        let clients = await apiWithConfig.get('/clients');
        clients.data.forEach((user: UserType) => {
            user.userType = "Client"
        })
        return clients;
    },
    activate: (id:string) => {
        return apiWithConfig.post(`/clients/activate/${id}`);
    },

    deactivate: (id:string) => {
        return apiWithConfig.post(`/clients/deactivate/${id}`);
    },
    modify: (user:ClientType) => {
        let userCopy:any = {user};
        delete userCopy['id'];
        userCopy.password='';
        return apiWithConfig.put(`/client/clientModify/${user.id}`,userCopy)
    }
}