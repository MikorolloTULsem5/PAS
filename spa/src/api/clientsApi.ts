import {apiWithConfig} from "./api.config";
import {ApiResponseType} from "../types/ApiResponseType";
import {AccountTypeEnum, ClientType, NewClientType, UserType} from "../types/Users";

export const clientsApi = {
    getClients: async (): Promise<ApiResponseType<ClientType[]>> => {
        let clients = await apiWithConfig.get('/clients');
        clients.data.forEach((user: UserType) => {
            user.userType = AccountTypeEnum.CLIENT
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
        let userCopy:any = {...user};
        delete userCopy['id'];
        delete userCopy['userType'];
        //userCopy.password='';
        return apiWithConfig.put(`/clients/modifyClient/${user.id}`,userCopy)
    },
    create: (user:NewClientType): ApiResponseType<any>  => {
        let userCopy:any = {...user};
        delete userCopy['userType'];
        return apiWithConfig.post(`/clients/addClient`,userCopy);
    }
}