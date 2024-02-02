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
        delete userCopy['eTag'];
        //userCopy.password='';
        return apiWithConfig.put(`/clients/modifyClient`,userCopy)
    },
    changePassword: (oldPassword:string, newPassword: string, newPasswordConfirm: string) => {
        return apiWithConfig.patch(`/clients/changePassword/me`,
            {actualPassword: oldPassword, newPassword: newPassword, confirmationPassword: newPasswordConfirm})
    },
    changeDetails: (user:ClientType) => {
        let userCopy:any = {...user};
        delete userCopy['userType'];
        delete userCopy['eTag'];
        return apiWithConfig.put(`/clients/modifyClient/me`,userCopy, {headers:{"If-Match":user.eTag}})
    },
    create: (user:NewClientType): ApiResponseType<any>  => {
        let userCopy:any = {...user};
        delete userCopy['userType'];
        return apiWithConfig.post(`/clients/addClient`,userCopy);
    }
}