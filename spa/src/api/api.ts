import {ApiResponseType} from "../types/ApiResponseType";
import {UserType} from "../types/Users";
import {apiWithConfig} from "./api.config";

export const api = {
    logIn: (login: string, password: string): ApiResponseType<UserType> => {
        return apiWithConfig.put('/auth/authenticate', { login, password })
    },
    logOut: (): ApiResponseType<UserType> => {
        return apiWithConfig.put('/accounts/log_out')
    },
    getCurrentAccount: (): ApiResponseType<UserType> => {
        return apiWithConfig.get('/accounts/existing_account')
    },
}