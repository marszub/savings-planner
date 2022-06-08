export class OneTimeEventCreateForm {
    constructor(title, eventType, amount, date) {
        this.title = title;
        this.eventType = eventType;
        this.amount = amount;
        this.date = date;
    }
}

export class CyclicEventCreateForm {
    constructor(title, eventType, amount, begins, ends, cycleLength, cycleBase) {
        this.title = title;
        this.eventType = eventType;
        this.amount = amount;
        this.begins = begins;
        this.ends = ends;
        this.cycleLength = cycleLength;
        this.cycleBase = cycleBase;
    }
}
