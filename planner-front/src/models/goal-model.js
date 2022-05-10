export class GoalModel {
  constructor(id, title, amount, priority, subGoals) {
    this.id = id;
    this.title = title;
    this.amount = amount;
    this.priority = priority;
    this.subGoals = subGoals;
  }
}
