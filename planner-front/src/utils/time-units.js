export const DAY = 7;
export const MONTH = 5;
export const YEAR = 6;

export function timeFormatted(timeLength, timeUnit) {
  switch (timeUnit) {
    case DAY:
      return timeLength === 1 ? '1 day' : `${timeLength} days`;
    case MONTH:
      return timeLength === 1 ? '1 month' : `${timeLength} months`;
    case YEAR:
      return timeLength === 1 ? '1 year' : `${timeLength} years`;
    default:
      return '';
  }
}
