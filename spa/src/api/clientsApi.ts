import {apiWithConfig} from "./api.config";
import {ApiResponseType} from "../types/ApiResponseType";
import {ClientType, UserType} from "../types/Users";

export const clientsApi = {
    getClients: (): ApiResponseType<ClientType[]> => {
        return apiWithConfig.get('/clients')
    },
    activate: (id:string) => {
        apiWithConfig.put(`/clients/activate/${id}`);
    },

    deactivate: (id:string) => {
        apiWithConfig.put(`/clients/deactivate/${id}`);
    }
}