import {apiWithConfig} from "./api.config";
import {ApiResponseType} from "../types/ApiResponseType";
import {UserType} from "../types/Users";

export const resAdminsApi = {
    getResAdmins: (): ApiResponseType<UserType[]> => {
        return apiWithConfig.get('/resAdmins')
    },

    activate: (id:string) => {
        apiWithConfig.put(`/resAdmins/activate/${id}`);
    },

    deactivate: (id:string) => {
        apiWithConfig.put(`/resAdmins/deactivate/${id}`);
    }

}
