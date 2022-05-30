import {httpService} from "./http-service";
import {CreateGoalRequest} from "../requests/create-goal-request";
import {GoalPriorityUpdateModel} from "../models/goal-priority-update-model";
import {HTTP_CONFLICT, HTTP_CREATED, HTTP_NO_CONTENT, HTTP_NOT_FOUND, HTTP_OK} from "../utils/http-status";
import {goalCompare} from "../utils/goal-compare";
import {MAX_INT32} from "../utils/money-validators";
import {moneyFormatter} from "../utils/money-formatter";
import {CreateSubGoalRequest} from "../requests/create-sub-goal-request";

export const goalService = {

  _goals: [],
  _changeListeners: [],

  addChangeListener(onGoalsChange) {
    this._changeListeners.push(onGoalsChange);
    onGoalsChange([...this._goals]);
  },

  removeChangeListener(onGoalsChange) {
    this._changeListeners = this._changeListeners.filter(listener => listener !== onGoalsChange);
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
              return httpService.onUnexpectedHttpStatus(res.status);
          }
          return res;
        })
  },

  create(formModel) {
    const lastGoal = this._goals[this._goals.length - 1];
    const priority = lastGoal ? lastGoal.priority - 1 : MAX_INT32;

    return httpService.post(
        '/goals',
        new CreateGoalRequest(
            formModel.title,
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
              return httpService.onUnexpectedHttpStatus(res.status);
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
              return httpService.onUnexpectedHttpStatus(res.status);
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
              return httpService.onUnexpectedHttpStatus(res.status);
          }
          return res;
        });
  },

  createSubGoal(parentGoalId, formModel) {
    return httpService.post(
        `/goals/${parentGoalId}/sub-goals`,
          new CreateSubGoalRequest(
              formModel.title,
              moneyFormatter.mapStringToPenniesNumber(formModel.amount)
          )
        )
        .then(async res => {
          switch (res.status) {
            case HTTP_CREATED:
              this._replace_goal(parentGoalId, res.body);
              this._notifyChangeListeners();
              break;
            case HTTP_NOT_FOUND:
              await this.getList();
              break;
            default:
              return httpService.onUnexpectedHttpStatus(res.status);
          }
          return res;
        });
  },

  deleteSubGoal(parentGoalId, subGoalId) {
    return httpService.delete(`/goals/${parentGoalId}/sub-goals/${subGoalId}`)
        .then(async res => {
          switch (res.status) {
            case HTTP_OK:
              this._replace_goal(parentGoalId, res.body);
              this._notifyChangeListeners();
              break;
            case HTTP_NOT_FOUND:
              await this.getList();
              break;
            default:
              return httpService.onUnexpectedHttpStatus(res.status);
          }
          return res;
        });
  },

  _notifyChangeListeners() {
    this._changeListeners.forEach(onChange => onChange([...this._goals]));
  },

  _replace_goal(goalId, newGoal) {
    const goalIndex = this._goals.findIndex(goal => goal.id === goalId);
    this._goals[goalIndex] = newGoal;
  }
}
