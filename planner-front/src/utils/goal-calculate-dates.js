export const goalCalculateDates = (goals, balance, events) => {
    if (!goals.length) return [];

    const goalDates = [];
    let eventsAmount = balance;

    let goalIndex = 0;
    let goal = goals[goalIndex];

    for (let i = 0; i < events.length; i++) {
        const event = events[i];
        eventsAmount += event.amount;

        while (goal.amount <= eventsAmount) {
            goalDates.push(new Date(event.timestamp));
            eventsAmount -= goal.amount;

            goalIndex += 1;
            if (goalIndex >= goals.length) return goalDates;
            goal = goals[goalIndex];
        }
    }

    return goalDates;
}
