import {httpService} from "./http-service";
import {CreateGoalRequest} from "../requests/create-goal-request";
import {moneyFormatter} from "../utils/money-formatter";
import {GoalPriorityUpdateModel} from "../models/goal-priority-update-model";

export const goalService = {
    getList() {
        return httpService.get("/goals");
    },

    create(formModel, priority) {
        return httpService.post(
            '/goals',
            new CreateGoalRequest(
                formModel.title,
                moneyFormatter.mapStringToPenniesNumber(formModel.amount),
                priority
            )
        );
    },

    updatePriority(goalPriorityUpdatesMap) {
        const newPriorities = [...goalPriorityUpdatesMap.entries()]
            .map(entry => new GoalPriorityUpdateModel(entry[0], entry[1]));
        return httpService.patch('/goals', {newPriorities});
    },

    delete(id) {
        return httpService.delete(`/goals/${id}`);
    },

    createSubGoal(goalId, subGoalTitle) {
        return httpService.post(
            `/goals/${goalId}/sub-goals`,
            {title: subGoalTitle}
        );
    },

    deleteSubGoal(goalId, subGoalId) {
        return httpService.delete(
            `/goals/${goalId}/sub-goals/${subGoalId}`
        );
    }
}
