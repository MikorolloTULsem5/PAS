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

    getMe: async (): Promise<ApiResponseType<ClientType>> => {
        let client = await apiWithConfig.get(`/clients/get/me`);
        client.data.userType = AccountTypeEnum.CLIENT;
        client.data.eTag = client.headers['etag'];
        console.log("eTag "+client.data.eTag)
        return client;
    },

    activate: (id:string) => {
        return apiWithConfig.post(`/clients/activate/${id}`);
    },

    deactivate: (id:string) => {
        return apiWithConfig.post(`/clients/deactivate/${id}`);
    },
    modify: (user:ClientType) => {
        let userCopy:any = {...user};
        delete userCopy['userType'];
        //userCopy.password='';
        return apiWithConfig.put(`/clients/modifyClient`,userCopy)
    },
    create: (user:NewClientType): ApiResponseType<any>  => {
        let userCopy:any = {...user};
        delete userCopy['userType'];
        return apiWithConfig.post(`/clients/addClient`,userCopy);
    }
}