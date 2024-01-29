import {ApiResponseType} from "../types/ApiResponseType";
import {UserType} from "../types/Users";
import {apiWithConfig} from "./api.config";
import {TokenPayload} from "../types/TokenPayload";

export const api = {
    logIn: (login: string, password: string): ApiResponseType<TokenPayload> => {
        return apiWithConfig.post('/auth/authenticate', { login, password })
    },
    logOut: (): ApiResponseType<UserType> => {
        return apiWithConfig.post('/accounts/log_out')
    },
    getCurrentAccount: (): ApiResponseType<UserType> => {
        return apiWithConfig.post('/accounts/existing_account')
    },
}