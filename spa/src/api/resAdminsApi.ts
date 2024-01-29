import {apiWithConfig} from "./api.config";
import {ApiResponseType} from "../types/ApiResponseType";
import {AccountTypeEnum, NewUserType, UserType} from "../types/Users";

export const resAdminsApi = {
    getResAdmins: async (): Promise<ApiResponseType<UserType[]>> => {
        let resAdmins = await apiWithConfig.get('/resAdmins');
        resAdmins.data.forEach((user: UserType) => {
            user.userType = AccountTypeEnum.RESADMIN;
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
        let userCopy:any = {...user};
        delete userCopy['id'];
        delete userCopy['userType'];
        //userCopy.password='';
        return apiWithConfig.put(`/resAdmins/modifyResAdmin/${user.id}`,userCopy)
    },

    create: (user:NewUserType): ApiResponseType<any>  => {
        let userCopy:any = {...user};
        delete userCopy['userType'];
        return apiWithConfig.post(`/resAdmins/addResAdmin`,userCopy);
    }

}
