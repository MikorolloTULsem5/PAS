export interface ReservationType {
    beginTime: string
    client: ReservationClientType
    court: CourtType
    endTime: string | null
    id: string
    reservationCost: number
    reservationHours: number
}

export interface ReservationClientType {
    archive:boolean,
    id:string,
    login:string
    clientTypeName: "normal" | "coach" | "athlete"
    firstName: string
    lastName: string
}

export interface CourtType {
    archive: boolean
    area: number
    baseCost: number
    courtNumber: number
    id: string
    rented: boolean
}

export interface NewReservationType {
    courtId:string
    clientId:string
}