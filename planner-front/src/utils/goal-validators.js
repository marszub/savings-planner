export const goalValidators = {
    validateTitle(title) {
        if (title.length < 3 || title.length > 100) {
            return 'Title\'s length must be between 3 and 100!';
        }
        return '';
    },
}
