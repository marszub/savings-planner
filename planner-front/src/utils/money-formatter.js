export const moneyFormatter = {
  mapStringToPenniesNumber(money) {
    return Math.round(Number.parseFloat(money) * 100);
  },

  mapPenniesNumberToString(pennies) {
    return (pennies / 100).toFixed(2);
  }
}
