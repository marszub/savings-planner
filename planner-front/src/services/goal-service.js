import {httpService} from "./http-service";
import {CreateGoalRequest} from "../requests/create-goal-request";
import {moneyFormatter} from "../utils/money-formatter";

export const goalService = {
    getList() {
        return httpService.get("/goals");
    },

    create(formModel, priority) {
        return httpService.post(
            "/goals",
            new CreateGoalRequest(
                formModel.title,
                moneyFormatter.mapStringToPenniesNumber(formModel.amount),
                priority
            )
        );
    },

    delete(id) {
        return httpService.delete(`/goals/${id}`);
    }
}
