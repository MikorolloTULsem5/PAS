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

    getMe: async (): Promise<ApiResponseType<UserType>> => {
        let resAdmin = await apiWithConfig.get(`/resAdmins/get/me`);
        resAdmin.data.userType = AccountTypeEnum.RESADMIN
        return resAdmin;
    },

    activate: (id:string) => {
        apiWithConfig.post(`/resAdmins/activate/${id}`);
    },

    deactivate: (id:string) => {
        apiWithConfig.post(`/resAdmins/deactivate/${id}`);
    },
    modify: (user:UserType): ApiResponseType<any> => {
        let userCopy:any = {...user};
        delete userCopy['userType'];
        //userCopy.password='';
        return apiWithConfig.put(`/resAdmins/modifyResAdmin`,userCopy)
    },
    changePassword: (oldPassword:string, newPassword: string, newPasswordConfirm: string) => {
        return apiWithConfig.patch(`/resAdmins/changePassword/me`,
            {actualPassword: oldPassword, newPassword: newPassword, confirmationPassword: newPasswordConfirm})
    },

    create: (user:NewUserType): ApiResponseType<any>  => {
        let userCopy:any = {...user};
        delete userCopy['userType'];
        return apiWithConfig.post(`/resAdmins/addResAdmin`,userCopy);
    }

}
