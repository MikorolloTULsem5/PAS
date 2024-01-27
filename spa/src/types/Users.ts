export interface UserType {
    archive:boolean,
    id:string,
    login:string
    userType: "Client" | "Admin" | "Resource admin" | null
}

export interface ClientType extends UserType{
    clientTypeName: "normal" | "coach" | "athlete",
    firstName: string,
    lastName: string
}

export interface NewUserType {
    login:string,
    password:string
    userType: "Client" | "Admin" | "Resource admin" | null
}

export interface NewClientType extends NewUserType{
    clientTypeName: "normal" | "coach" | "athlete",
    firstName: string,
    lastName: string
}