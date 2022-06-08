export const eventValidators = {
  validateTitle(title) {
    if (title.length < 3 || title.length > 100) {
      return 'Title\'s length must be between 3 and 100!';
    }
    return '';
  },

  validateCycleLength(cycleLength) {
    if (cycleLength < 1 || cycleLength > 999) {
      return 'Cycle length must be between 1 and 999!';
    }
    return '';
  },

  validateBeginEnd(begin, end) {
    if (begin >= end) {
      return 'Begin date must be before end date';
    }
    return '';
  }
}
