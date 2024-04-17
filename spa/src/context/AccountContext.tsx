import {UserType} from "../types/Users";
import {createContext, ReactNode, useContext, useEffect, useState} from "react";

interface AccountState {
    account: UserType | null
    setAccount: (item: UserType | null) => void
}
const AccountStateContext = createContext<AccountState | null>(null)
export const AccountStateContextProvider = ({ children }: { children: ReactNode }) => {
    const [account, setAccount] = useState<UserType | null>(null);

    useEffect(() => {
    }, [account]);
    return (
        <AccountStateContext.Provider
                value={{ account, setAccount}}>
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