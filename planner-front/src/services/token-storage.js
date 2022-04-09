export const tokenStorage = {
    _accessToken: "",

    get accessToken() {
        return this._accessToken;
    },

    set accessToken(newToken) {
        this._accessToken = newToken;
    }
}