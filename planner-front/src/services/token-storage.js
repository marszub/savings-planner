export const tokenStorage = {
    _accessToken: "",

    get accessToken() {
        return this._accessToken;
    },

    set accessToken(newToken) {
        this._accessToken = newToken;
        localStorage.setItem("accessToken", newToken);
    },

    revokeToken() {
        this._accessToken = "";
        localStorage.removeItem("accessToken");
    }
}