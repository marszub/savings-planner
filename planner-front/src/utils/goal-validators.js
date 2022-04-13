export const goalValidators = {
    validateTitle(title) {
        if (title.length < 3 || title.length > 100) {
            return "Title's length must be between 3 and 100!";
        }
        return "";
    },

    validateAmount(amount) {
        if (amount < 5 || amount > 1_000_000_000_000) {
            return "Amount must be between 5 and 1,000,000,000,000!";
        }
        return "";
    }
}
