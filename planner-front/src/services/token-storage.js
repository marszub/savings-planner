export const ACCESS_TOKEN = "accessToken";

export const tokenStorage = {
    _accessToken: null,

    get accessToken() {
        return this._accessToken;
    },

    set accessToken(newToken) {
        this._accessToken = newToken;
        localStorage.setItem(ACCESS_TOKEN, newToken);
    },

    revokeToken() {
        this._accessToken = null;
        localStorage.removeItem(ACCESS_TOKEN);
    }
}
