import axios from 'axios'
export const API_URL = "https://localhost:8080/api" //process.env.REACT_APP_API_URL
export const TIMEOUT_IN_MS = 30000
export const DEFAULT_HEADERS = {
    Accept: 'application/json',
    'Content-type': 'application/json',
}
export const apiWithConfig = axios.create({
    baseURL: API_URL,
    timeout: TIMEOUT_IN_MS,
    headers: DEFAULT_HEADERS,
})

apiWithConfig.interceptors.response.use(
    (response) => response,
    (error) => {
        const status = error.response?.status
        if (status === 401 || status === 403 || status === 404) {
            localStorage.removeItem('token')
        }
        return Promise.reject(error)
    },
)

apiWithConfig.interceptors.request.use((config) => {
    let token = window.localStorage.getItem('token');
    token = (token && token !== "null") ? token : null;
    if (token && config.headers) {config.headers.Authorization = `Bearer ${token}`};
    return config
})