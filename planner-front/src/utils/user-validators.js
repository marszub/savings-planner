export const userValidators = {
    validateNick(nick) {
        if (!nickRegex.test(nick)) {
            return "Nick's length must be between 3 and 16 and must consist only of letters, numbers and underscores!";
        }
        return "";
    },

    validateEmail(email) {
        if (!emailRegex.test(email)) {
            return "Email is invalid!";
        }
        return "";
    },

    validateLogin(login) {
        if (!nickRegex.test(login) && !emailRegex.test(login)) {
            return "Login is invalid!";
        }
        return "";
    },

    validatePassword(password) {
        if (password.length < 7 || password.length > 100 ) {
            return "Password's length must be between 7 and 100!";
        }
        return "";
    },

    validatePasswordsMatch(password, repeatPassword) {
        if (password !== repeatPassword) {
            return "Password and repeated password differ!";
        }
        return "";
    }
}

const nickRegex = /^\w{3,16}$/
const emailRegex = /^\S+@\S+(\.\S+)+$/
