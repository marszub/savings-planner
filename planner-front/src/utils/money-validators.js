import { moneyFormatter } from "./money-formatter";

export const MAX_INT32 = (2 ** 31) - 1;

export const moneyValidators = {
    validateAmount(amountString) {
        let validationMessage = this._validateAmountString(amountString);

        if (validationMessage) {
            return validationMessage;
        }

        const penniesNumber = moneyFormatter.mapStringToPenniesNumber(amountString);

        validationMessage = this._validateAmountInPennies(penniesNumber);
        if (validationMessage) {
            return validationMessage;
        }

        return '';
    },

    _validateAmountString(amount) {
        if (!amountStringRegex.test(amount)) {
            return `Amount must match one of the patterns: 5.14, 326.00 or 445!`;
        }
        return '';
    },

    _validateAmountInPennies(amount) {
        if (amount <= 0 || amount > MAX_INT32) {
            return `Amount must be between 0.01 and ${(MAX_INT32 / 100).toFixed(2)}!`;
        }
        return '';
    },
}

const amountStringRegex = /^(0|([1-9]\d*))(\.\d{2})?$/
