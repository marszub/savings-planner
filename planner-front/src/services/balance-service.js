import { httpService } from "./http-service";
import { UpdateBalanceRequest } from "../requests/update-balance-request";
import { moneyFormatter } from "../utils/money-formatter";
import {HTTP_NO_CONTENT, HTTP_OK} from "../utils/http-status";

export const balanceService = {
    _balance: 0,
    _changeListeners: [],

    addChangeListener(onChange) {
        this._changeListeners.push(onChange);
        onChange(this._balance);
    },

    removeChangeListener(onChange) {
        this._changeListeners = this._changeListeners.filter(listener => listener !== onChange);
    },

    _notifyChangeListeners() {
        this._changeListeners.forEach(onChange => onChange(this._balance));
    },

    getValue() {
        return httpService.get("/balance")
            .then(res => {
                switch (res.status) {
                    case HTTP_OK:
                        this._balance = res.body.balance;
                        this._notifyChangeListeners();
                        break;
                    default:
                        return httpService.onUnexpectedHttpStatus(res.status);
                }
                return res;
            });
    },

    update(formModel) {
        const body = new UpdateBalanceRequest(moneyFormatter.mapStringToPenniesNumber(formModel.balance));
        return httpService.put("/balance", body)
            .then(res => {
                switch (res.status) {
                    case HTTP_NO_CONTENT:
                        this._balance = body.balance;
                        this._notifyChangeListeners();
                        break;
                    default:
                        return httpService.onUnexpectedHttpStatus(res.status);
                }
                return res;
            });
    }
}
