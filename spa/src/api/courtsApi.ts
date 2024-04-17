import {apiWithConfig} from "./api.config";
import {ApiResponseType} from "../types/ApiResponseType";
import {CourtType} from "../types/ReservationsTypes";

export const courtsApi = {
    getCourts: ():ApiResponseType<CourtType[]> => {
        return apiWithConfig.get('/courts');
    },
    getCourtById: (courtId:string):ApiResponseType<CourtType> => {
        return apiWithConfig.get(`/courts/${courtId}`);
    }
}