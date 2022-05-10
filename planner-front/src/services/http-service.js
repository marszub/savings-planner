import { HTTP_NO_CONTENT, HTTP_UNAUTHORIZED } from "../utils/http-status";
import { BACKEND_URL } from "../config";
import { tokenStorage } from "./token-storage";
import { userService } from "./user-service";

export const httpService = {
    get(path, queryParams) {
        return request(`${path}?${new URLSearchParams(queryParams)}`, 'GET');
    },

    post(path, body) {
        return request(path, 'POST', body);
    },

    put(path, body) {
        return request(path, 'PUT', body);
    },

    patch(path, body) {
        return request(path, 'PATCH', body);
    },

    delete(path, body) {
        return request(path, 'DELETE', body);
    }
}

function request(path, method, body=null) {
    return fetch(`${BACKEND_URL}${path}`, {
        method: method,
        headers: {
            'Content-Type': 'application/json; charset=UTF-8',
            ...(tokenStorage.accessToken) && {'Authorization': 'Bearer ' + tokenStorage.accessToken},
        },
        credentials: 'include',
        body: body && JSON.stringify(body, replacer),
    })
        .then(onResponse);
}

function onResponse(res) {
    if (res.status === HTTP_NO_CONTENT) {
        return new ResponseShort(res.status, res.body);
    }
    if (res.status === HTTP_UNAUTHORIZED && res.url !== `${BACKEND_URL}/auth/access-token`) {
        console.log("User unauthorized");
        userService.signOut();
    }
    return new Promise((resolve, reject) => res.json()
            .then(body => resolve(new ResponseShort(res.status, body)))
            .catch(err => reject(err)));
}

function replacer(key, value) {
    if (value instanceof Map) {
        return Object.fromEntries(value.entries());
    } else if (value instanceof Set) {
        return Array.from(value);
    } else if (value === null) {
        return undefined;
    } else {
        return value;
    }
}

class ResponseShort {
    constructor(httpStatus, body) {
        this.status = httpStatus;
        this.body = body;
    }
}
