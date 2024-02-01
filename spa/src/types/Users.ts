export enum AccountTypeEnum{
    ADMIN = "Admin",
    CLIENT = "Client",
    RESADMIN = "Resource admin"
}

export interface UserType {
    archive:boolean,
    id:string,
    login:string
    token?: string
    userType: AccountTypeEnum | null
}

export interface ClientType extends UserType{
    clientTypeName: "normal" | "coach" | "athlete",
    firstName: string,
    lastName: string,
    eTag?: string
}

export interface NewUserType {
    login:string,
    password:string
    userType: AccountTypeEnum | null
}

export interface NewClientType extends NewUserType{
    clientTypeName: "normal" | "coach" | "athlete",
    firstName: string,
    lastName: string
}