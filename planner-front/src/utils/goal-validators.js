const MAX_INT32 = (2 ** 31) - 1

export const goalValidators = {
    validateTitle(title) {
        if (title.length < 3 || title.length > 100) {
            return "Title's length must be between 3 and 100!";
        }
        return "";
    },

    validateAmount(amount) {
        if (amount < 5 || amount > MAX_INT32) {
            return `Amount must be between 5 and ${MAX_INT32}!`;
        }
        return "";
    }
}
