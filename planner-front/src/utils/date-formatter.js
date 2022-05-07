export const dateFormatter = {
    formatDate(date) {
        return `${this._padTo2Digits(date.getMonth() + 1)}/${this._padTo2Digits(date.getDate())}/${date.getFullYear()}`;
    },

    _padTo2Digits(num) {
        return num.toString().padStart(2, '0');
    }
}
