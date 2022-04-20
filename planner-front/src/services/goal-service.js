import {httpService} from "./http-service";
import {CreateGoalRequest} from "../requests/create-goal-request";
import {moneyFormatter} from "../utils/money-formatter";

export const goalService = {
    getList() {
        return httpService.get("/goals");
    },

    create(formModel) {
        const body = new CreateGoalRequest(formModel.title, moneyFormatter.mapStringToPenniesNumber(formModel.amount));
        return httpService.post("/goal", body);
    },

    delete(id) {
        return httpService.delete(`/goal/${id}`);
    }
}