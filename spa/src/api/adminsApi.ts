import {apiWithConfig} from "./api.config";

export const adminsApi = {
    getAdmins: () => {
        return apiWithConfig.get('/admins')
    },
}