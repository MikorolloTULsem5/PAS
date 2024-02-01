import {UserType} from "../types/Users";
import {createContext, ReactNode, useContext, useEffect, useState} from "react";

interface AccountState {
    account: UserType | null
    setAccount: (item: UserType | null) => void
    token: string | null;
    setToken: (item: string | null) => void
}
const AccountStateContext = createContext<AccountState | null>(null)
export const AccountStateContextProvider = ({ children }: { children: ReactNode }) => {
    const [account, setAccount] = useState<UserType | null>(null);
    const [token, setToken] = useState<string | null>(null);
    useEffect(() => {
        if(token){
            localStorage.setItem('token',token)
        }
    }, [token]);

    useEffect(() => {
        console.log(account)
    }, [account]);
    return (
        <AccountStateContext.Provider
                value={{ account, setAccount, token, setToken }}>
            {children}
        </AccountStateContext.Provider>
    )
}
export const useAccountState = () => {
    const accountState = useContext(AccountStateContext)
    if (!accountState) {
        throw new Error('You forgot about AccountStateContextProvider!')
    }
    return accountState;
}