import {httpService} from "./http-service";
import {CreateGoalRequest} from "../requests/create-goal-request";
import {moneyFormatter} from "../utils/money-formatter";
import {GoalPriorityUpdateModel} from "../models/goal-priority-update-model";
import {HTTP_CONFLICT, HTTP_CREATED, HTTP_NO_CONTENT, HTTP_OK} from "../utils/http-status";
import {goalStorage} from "./goal-storage";
import {goalCompare} from "../utils/goal-compare";
import {GoalModel} from "../models/goal-model";

export const goalService = {

    _storage: goalStorage,

    getList() {
        return httpService.get("/goals")
            .then(res => {
                if (res.status === HTTP_OK) {
                    this._storage.setGoals(res.body.goals);
                }
                return res;
            });
    },

    create(formModel, priority) {
        return httpService.post(
            '/goals',
            new CreateGoalRequest(
                formModel.title,
                moneyFormatter.mapStringToPenniesNumber(formModel.amount),
                priority
            )
        ).then(res => {
            if (res.status === HTTP_CREATED) {
                this._storage.setGoals(prev => [res.body, ...prev].sort(goalCompare));
            } else if (res.status === HTTP_CONFLICT) {
                this.getList();
            }
            return res;
        });
    },

    updatePriority(goalPriorityUpdatesMap) {
        const newPriorities = [...goalPriorityUpdatesMap.entries()]
            .map(entry => new GoalPriorityUpdateModel(entry[0], entry[1]));

        return httpService.patch('/goals', {newPriorities})
            .then(res => {
                if (res.status === HTTP_NO_CONTENT) {
                    this._storage.setGoals(prev => prev
                        .map(goal => this._updatePriority(goal, goalPriorityUpdatesMap))
                        .sort(goalCompare)
                    );
                } else if (res.status === HTTP_CONFLICT) {
                    this.getList();
                }
                return res;
            });
    },

    delete(id) {
        return httpService.delete(`/goals/${id}`)
            .then(res => {
                if (res.status === HTTP_NO_CONTENT) {
                    this._storage.setGoals(prev => prev.filter(goal => goal.id !== id));
                } else if (res.status === HTTP_CONFLICT) {
                    this.getList();
                }
                return res;
            });
    },

    createSubGoal(parentGoalId, subGoalTitle) {
        return httpService.post(`/goals/${goalId}/sub-goals`, {title: subGoalTitle})
            .then(res => {
                if (res.status === HTTP_CREATED) {
                    this._storage.setGoals(prev => prev
                        .map(goal => this._addSubGoal(goal, parentGoalId, res.body))
                    );
                    return res;
                } else if (res.status === HTTP_CONFLICT) {
                    this.getList();
                }
                return res;
            });
    },

    deleteSubGoal(parentGoalId, subGoalId) {
        return httpService.delete(`/goals/${goalId}/sub-goals/${subGoalId}`)
            .then(res => {
                if (res.status === HTTP_NO_CONTENT) {
                    this._storage.setGoals(prev => prev
                        .map(goal => this._deleteSubGoal(goal, parentGoalId, subGoalId))
                    )
                } else if (res.status === HTTP_CONFLICT) {
                    this.getList();
                }
                return res;
            });
    },

    _updatePriority(goal, goalPriorityUpdatesMap) {
        if (goalPriorityUpdatesMap.has(goal.id)) {
            return new GoalModel(goal.id, goal.title, goal.amount, goalPriorityUpdatesMap.get(goal.id), goal.subGoals)
        } else {
            return goal;
        }
    },

    _addSubGoal(goal, parentGoalId, subGoal) {
        if (goal.id === parentGoalId) {
            return new GoalModel(goal.id, goal.title, goal.amount, goal.priority, [...goal.subGoals, subGoal]);
        } else {
            return goal;
        }
    },

    _deleteSubGoal(goal, parentGoalId, subGoalId) {
        if (goal.id === parentGoalId) {
            return new GoalModel(goal.id, goal.title, goal.amount, goal.priority,
                goal.subGoals.filter(subGoal => subGoal.id !== subGoalId)
            );
        } else {
            return goal;
        }
    }
}
