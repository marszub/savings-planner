import { tokenStorage } from "./token-storage";
import { authService } from "./auth-service";

export const userService = {
    signUp(formModel) {
        return authService.signUp(formModel);
    },

    signIn(formModel) {
        return authService.signIn(formModel);
    },

    signOut() {
        tokenStorage.revokeToken();
        window.location.replace("/sign-in");
    }
}
