import { httpService } from "./http-service";
import { SignInRequest } from "../requests/sign-in-request";
import { SignUpRequest } from "../requests/sign-up-request";
import { HTTP_CREATED, HTTP_OK } from "../utils/http-status";
import { tokenStorage } from "./token-storage";

export const authService = {
    signUp(formModel) {
        const body = new SignUpRequest(formModel.nick, formModel.email, formModel.password);
        return httpService.post("/auth/users", body)
            .then(res => this._try_set_access_token(res));
    },

    signIn(formModel) {
        const body = new SignInRequest(formModel.login, formModel.password);
        return httpService.post("/auth/access-token", body)
            .then(res => this._try_set_access_token(res));
    },

    _try_set_access_token(res) {
        if (res.status === HTTP_OK || res.status === HTTP_CREATED) {
            tokenStorage.accessToken = res.body.accessToken;
        }
        return res;
    }
}
