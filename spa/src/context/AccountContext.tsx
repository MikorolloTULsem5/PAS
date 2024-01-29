import {UserType} from "../types/Users";
import {createContext, ReactNode, useContext, useEffect, useState} from "react";

interface AccountState {
    account: UserType | null
    setAccount: (item: UserType | null) => void
    isLoggingIn: boolean
    setIsLoggingIn: (value: boolean) => void
    isFetching: boolean
    setIsFetching: (value: boolean) => void
}
const AccountStateContext = createContext<AccountState | null>(null)
export const AccountStateContextProvider = ({ children }: { children: ReactNode }) => {
    const [account, setAccount] = useState<UserType | null>(null)
    const [isLoggingIn, setIsLoggingIn] = useState(false)
    const [isFetching, setIsFetching] = useState(true)
    useEffect(() => {
        if (account?.token) {
            localStorage.setItem('token', JSON.stringify(account.token))
        }
    }, [account])
    return (
        <AccountStateContext.Provider
                value={{ account, setAccount, isLoggingIn, setIsLoggingIn, isFetching, setIsFetching }}>
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