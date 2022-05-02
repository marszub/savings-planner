import { tokenStorage } from "./token-storage";
import { authService } from "./auth-service";

export const USER_NICK = "userNick";

export const userService = {
    signUp(formModel) {
        localStorage.setItem(USER_NICK, formModel.nick);
        return authService.signUp(formModel);
    },

    signIn(formModel) {
        localStorage.setItem(USER_NICK, formModel.login);
        return authService.signIn(formModel);
    },

    signOut() {
        tokenStorage.revokeToken();
        window.location.replace("/sign-in");
    }
}
