import {apiWithConfig} from "./api.config";
import {ApiResponseType} from "../types/ApiResponseType";
import {UserType} from "../types/Users";

export const adminsApi = {
    getAdmins: (): ApiResponseType<UserType[]> => {
        return apiWithConfig.get('/admins')
    },

    activate: (id:string) => {
        apiWithConfig.put(`/admins/activate/${id}`);
    },

    deactivate: (id:string) => {
        apiWithConfig.put(`/admins/deactivate/${id}`);
    }
}