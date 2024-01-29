import {Pathnames} from "./pathnames";
import {RouteType} from "../types/RouteType";
import HomePage from "../pages/HomePage";
import UsersPage from "../pages/UsersPage";
import AddUserPage from "../pages/AddUserPage";
import ClientsPage from "../pages/ClientsPage";
import CourtsPage from "../pages/CourtsPage";
import ReservationsPage from "../pages/ReservationsPage";
import LoginPage from "../pages/LoginPage";

export const PublicRoutes: RouteType[] = [
    {
        path: Pathnames.public.home,
        Component: HomePage
    },
    {
        path: Pathnames.public.login,
        Component: LoginPage
    }
]

export const AdminRoutes: RouteType[] = [
    {
        path: Pathnames.public.home,
        Component: HomePage
    },
    {
        path: Pathnames.admin.users,
        Component: UsersPage
    },
    {
        path: Pathnames.admin.clients,
        Component: ClientsPage
    },
    {
        path: Pathnames.admin.courts,
        Component: CourtsPage
    },
    {
        path: Pathnames.admin.reservations,
        Component: ReservationsPage
    },
    {
        path: Pathnames.admin["add user"],
        Component: AddUserPage
    }
]

export const ClientRoutes: RouteType[] = [
    {
        path: Pathnames.client.home,
        Component: HomePage
    },
    {
        path: Pathnames.client.users,
        Component: UsersPage
    },
    {
        path: Pathnames.client.clients,
        Component: ClientsPage
    },
    {
        path: Pathnames.client.courts,
        Component: CourtsPage
    },
    {
        path: Pathnames.client.reservations,
        Component: ReservationsPage
    },
    {
        path: Pathnames.client["add user"],
        Component: AddUserPage
    }
]

export const ResAdminRoutes: RouteType[] = [
    {
        path: Pathnames.resAdmin.home,
        Component: HomePage
    },
    {
        path: Pathnames.resAdmin.users,
        Component: UsersPage
    },
    {
        path: Pathnames.resAdmin.clients,
        Component: ClientsPage
    },
    {
        path: Pathnames.resAdmin.courts,
        Component: CourtsPage
    },
    {
        path: Pathnames.resAdmin.reservations,
        Component: ReservationsPage
    },
    {
        path: Pathnames.resAdmin["add user"],
        Component: AddUserPage
    }
]