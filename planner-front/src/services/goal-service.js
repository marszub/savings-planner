import {httpService} from "./http-service";
import {CreateGoalRequest} from "../requests/create-goal-request";
import {moneyFormatter} from "../utils/money-formatter";
import {GoalPriorityUpdateModel} from "../models/goal-priority-update-model";
import {HTTP_CONFLICT, HTTP_CREATED, HTTP_NO_CONTENT, HTTP_NOT_FOUND, HTTP_OK} from "../utils/http-status";
import {goalCompare} from "../utils/goal-compare";
import {navigation} from "../utils/navigation";

export const goalService = {

  _goals: [],
  _changeListeners: [],

  addChangeListener(onGoalsChange) {
    this._changeListeners.push(onGoalsChange);
  },

  getList() {
    return httpService.get("/goals")
        .then(res => {
          switch (res.status) {
            case HTTP_OK:
              this._goals = res.body.goals;
              this._notifyChangeListeners();
              break;
            default:
              navigation.navigateError();
              break;
          }
          return res;
        })
  },

  create(formModel, priority) {
    return httpService.post(
        '/goals',
        new CreateGoalRequest(
            formModel.title,
            moneyFormatter.mapStringToPenniesNumber(formModel.amount),
            priority
        )
    )
        .then(async res => {
          switch (res.status) {
            case HTTP_CREATED:
              this._goals.push(res.body);
              this._goals.sort(goalCompare);
              this._notifyChangeListeners();
              break;
            case HTTP_CONFLICT:
              await this.getList();
              break;
            default:
              navigation.navigateError();
              break;
          }
          return res;
        })
  },

  updatePriority(goalPriorityUpdatesMap) {
    const newPriorities = [...goalPriorityUpdatesMap.entries()]
        .map(entry => new GoalPriorityUpdateModel(entry[0], entry[1]));

    return httpService.patch('/goals', {newPriorities})
        .then(async res => {
          switch (res.status) {
            case HTTP_NO_CONTENT:
              this._goals
                  .filter(goal => goalPriorityUpdatesMap.has(goal.id))
                  .forEach(goal => goal.priority = goalPriorityUpdatesMap.get(goal.id));
              this._goals.sort(goalCompare);
              this._notifyChangeListeners();
              break;
            case HTTP_CONFLICT:
            case HTTP_NOT_FOUND:
              await this.getList();
              break;
            default:
              navigation.navigateError();
              break;
          }
          return res;
        });
  },

  delete(id) {
    return httpService.delete(`/goals/${id}`)
        .then(async res => {
          switch (res.status) {
            case HTTP_NO_CONTENT:
              this._goals = this._goals.filter(goal => goal.id !== id);
              this._notifyChangeListeners();
              break;
            case HTTP_NOT_FOUND:
              await this.getList();
              break;
            default:
              navigation.navigateError();
              break;
          }
          return res;
        });
  },

  createSubGoal(parentGoalId, subGoalTitle) {
    return httpService.post(`/goals/${goalId}/sub-goals`, {title: subGoalTitle})
        .then(async res => {
          switch (res.status) {
            case HTTP_CREATED:
              const parentGoal = this._goals
                  .filter(goal => goal.id === parentGoalId)
                  .at(0);
              parentGoal.subGoals.push(res.body);
              this._notifyChangeListeners();
              break;
            case HTTP_NOT_FOUND:
              await this.getList();
              break;
            default:
              navigation.navigateError();
              break;
          }
          return res;
        });
  },

  deleteSubGoal(parentGoalId, subGoalId) {
    return httpService.delete(`/goals/${goalId}/sub-goals/${subGoalId}`)
        .then(async res => {
          switch (res.status) {
            case HTTP_NO_CONTENT:
              const parentGoal = this._goals
                  .filter(goal => goal.id === parentGoalId)
                  .at(0);
              parentGoal.subGoals = parentGoal.filter(subGoal => subGoal.id === subGoalId);
              this._notifyChangeListeners();
              break;
            case HTTP_NOT_FOUND:
              await this.getList();
              break;
            default:
              navigation.navigateError();
              break;
          }
          return res;
        });
  },

  _notifyChangeListeners() {
    this._changeListeners.forEach(onChange => onChange([...this._goals]));
  }
}
