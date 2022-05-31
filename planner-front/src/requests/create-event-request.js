export class CreateEventRequest {
    constructor(title, amount, timestamp) {
        this.title = title;
        this.amount = amount;
        this.isCyclic = false;
        this.timestamp = timestamp;
    }
}
