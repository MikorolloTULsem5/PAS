import axios from 'axios'
export const API_URL = "http://localhost:8080/api" //process.env.REACT_APP_API_URL
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