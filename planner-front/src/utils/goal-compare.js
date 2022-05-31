export const goalCompare = (g1, g2) => {
  return g2.priority - g1.priority;
};

export const subGoalCompare = (sg1, sg2) => {
  if (sg1.completed && !sg2.completed) {
    return 1;
  } else if (!sg1.completed && sg2.completed) {
    return -1;
  } else {
    return sg1.id - sg2.id;
  }
};

