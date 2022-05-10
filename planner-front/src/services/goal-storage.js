import {useState} from "react";

class GoalStorage {
  constructor() {
    const [goals, setGoals] = useState([]);
    this._goals = goals;
    this.setGoals = setGoals;
  }

  get goals() {
    return this._goals;
  }
}

export const goalStorage = new GoalStorage();
