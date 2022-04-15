export const moneyMapper = {
  mapStringToPenniesNumber(money) {
    return Math.trunc(Number.parseFloat(money) * 100);
  },

  mapPenniesNumberToString(pennies) {
    return (pennies / 100).toFixed(2);
  }
}
