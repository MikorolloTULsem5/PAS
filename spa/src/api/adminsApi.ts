import {apiWithConfig} from "./api.config";
import {ApiResponseType} from "../types/ApiResponseType";
import {AccountTypeEnum, NewUserType, UserType} from "../types/Users";

export const adminsApi = {
    getAdmins: async (): Promise<ApiResponseType<UserType[]>> => {
        let admins = await apiWithConfig.get('/admins');
        admins.data.forEach((user:UserType) => {
            user.userType = AccountTypeEnum.ADMIN
        })
        return admins;
    },

    getAdminByLogin: async (login:string): Promise<ApiResponseType<UserType>> => {
        let admin = await apiWithConfig.get(`/admins/get?login=${login}`);
        admin.data.userType = AccountTypeEnum.ADMIN
        return admin;
    },

    activate: (id:string) => {
        return apiWithConfig.post(`/admins/activate/${id}`);
    },

    deactivate: (id:string) => {
        return apiWithConfig.post(`/admins/deactivate/${id}`);
    },

    modify: (user:UserType): ApiResponseType<any> => {
        let userCopy:any = {...user};
        delete userCopy['id'];
        delete userCopy['userType'];
        //userCopy.password='';
        return apiWithConfig.put(`/admins/modifyAdmin/${user.id}`,userCopy);
    },

    create: (user:NewUserType): ApiResponseType<any>  => {
        let userCopy:any = {...user};
        delete userCopy['userType'];
        return apiWithConfig.post(`/admins/addAdmin`,userCopy);
    }
}