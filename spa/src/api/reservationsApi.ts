import {apiWithConfig} from "./api.config";
import {ApiResponseType} from "../types/ApiResponseType";
import {NewReservationType, ReservationType} from "../types/ReservationsTypes";

export const reservationsApi = {
    getReservationsByClientId: (clientId: string): ApiResponseType<ReservationType[]> => {
        return apiWithConfig.get(`/reservations/clientReservation?clientId=${clientId}`);
    },

    getReservationsById: (reservationId: string): ApiResponseType<ReservationType> => {
        return apiWithConfig.get(`/reservations/${reservationId}`);
    },

    getCurrentReservations: (): ApiResponseType<ReservationType[]> => {
        return apiWithConfig.get(`/reservations`);
    },

    getArchivedReservations: (): ApiResponseType<ReservationType[]> => {
        return apiWithConfig.get(`/reservations/archive`);
    },

    getAllReservations: async (): Promise<ReservationType[]> => {
        let current = (await reservationsApi.getCurrentReservations()).data
        let archived = (await reservationsApi.getArchivedReservations()).data
        return current.concat(archived);
    },

    returnCourt: (courtId: string): ApiResponseType<any> => {
        return apiWithConfig.post(`/reservations/returnCourt?courtId=${courtId}`)
    },

    create: (newReservation: NewReservationType): ApiResponseType<any> => {
        return apiWithConfig.post(`/reservations/addReservation?clientId=${newReservation.clientId}&courtId=${newReservation.courtId}`)
    }

}