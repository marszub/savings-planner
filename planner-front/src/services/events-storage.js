export const EventStorge = {
    _accessEvents: [],

    get accessEvents() {
        return this._accessEvents;
    },

    set accessEvents(data) {
        this._accessEvents = data;
    },

    revokeEvents() {
        this._accessEvents = [];
    }
}