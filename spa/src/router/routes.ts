import {Pathnames} from "./pathnames";
import {RouteType} from "../types/RouteType";
import HomePage from "../pages/HomePage";
import UsersPage from "../pages/UsersPage";

export const publicRoutes: RouteType[] = [
    {
        path: Pathnames.public.home,
        Component: HomePage
    },
    {
        path: Pathnames.public.users,
        Component: UsersPage
    }
]