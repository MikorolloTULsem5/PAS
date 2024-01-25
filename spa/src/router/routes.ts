import {Pathnames} from "./pathnames";
import {RouteType} from "../types/RouteType";
import HomePage from "../pages/HomePage";
import UsersPage from "../pages/UsersPage";
import AddUserPage from "../pages/AddUserPage";
import ClientsPage from "../pages/ClientsPage";
import CourtsPage from "../pages/CourtsPage";
import ReservationsPage from "../pages/ReservationsPage";

export const publicRoutes: RouteType[] = [
    {
        path: Pathnames.public.home,
        Component: HomePage
    },
    {
        path: Pathnames.public.users,
        Component: UsersPage
    },
    {
        path: Pathnames.public.clients,
        Component: ClientsPage
    },
    {
        path: Pathnames.public.courts,
        Component: CourtsPage
    },
    {
        path: Pathnames.public.reservations,
        Component: ReservationsPage
    },
    {
        path: Pathnames.public["add user"],
        Component: AddUserPage
    }
]