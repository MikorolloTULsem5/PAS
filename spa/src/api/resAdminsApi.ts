import {apiWithConfig} from "./api.config";
import {ApiResponseType} from "../types/ApiResponseType";
import {UserType} from "../types/Users";

export const resAdminsApi = {
    getResAdmins: async (): Promise<ApiResponseType<UserType[]>> => {
        let resAdmins = await apiWithConfig.get('/resAdmins');
        resAdmins.data.forEach((user: UserType) => {
            user.userType = "Resource admin"
        })
        return resAdmins;
    },

    activate: (id:string) => {
        apiWithConfig.post(`/resAdmins/activate/${id}`);
    },

    deactivate: (id:string) => {
        apiWithConfig.post(`/resAdmins/deactivate/${id}`);
    },
    modify: (user:UserType): ApiResponseType<any> => {
        let userCopy:any = {user};
        delete userCopy['id'];
        userCopy.password='';
        return apiWithConfig.put(`/resAdmins/modifyResAdmin/${user.id}`,userCopy)
    }

}
