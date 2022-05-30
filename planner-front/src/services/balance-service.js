import { httpService } from "./http-service";
import { UpdateBalanceRequest } from "../requests/update-balance-request";
import { moneyFormatter } from "../utils/money-formatter";

export const balanceService = {
    getValue() {
        return httpService.get("/balance");
    },

    update(formModel) {
        const body = new UpdateBalanceRequest(moneyFormatter.mapStringToPenniesNumber(formModel.balance));
        return httpService.put("/balance", body);
    }
}
