export class CreateOneTimeEventRequest {
    constructor(title, amount, timestamp) {
        this.title = title;
        this.amount = amount;
        this.isCyclic = false;
        this.timestamp = timestamp;
    }
}

export class CreateCyclicEventRequest {
    constructor(title, amount, begin, end, cycleBase, cycleLength) {
        this.title = title;
        this.amount = amount;
        this.isCyclic = true;
        this.begin = begin;
        this.cycleEnd = end;
        this.cycleBase = cycleBase;
        this.cycleLength = cycleLength;
    }
}
