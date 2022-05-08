import { httpService } from "./http-service";
import { EventStorge } from "./events-storage";
import { HTTP_OK } from "../utils/http-status";

function changeTimestamp(EventData) {
  for (let event of EventData) {
    let x = new Date(event.timestamp * 1000);
    event.timestamp = x;
    event.amount /= 100;
  }
  EventData = sortEvents(EventData);
  return EventData;
}

function sortEvents(events) {
  return events.sort(compare);
}

function compare(eventA, eventB) {
  let a = new Date(eventA.timestamp);
  let b = new Date(eventB.timestamp);
  if (a < b) return -1;
  else return 1;
}

export const eventService = {
  getEventsList() {
    return httpService
      .get("/events")
      .then((res) => this.setEvents(res))
      .catch((error) => console.log(error));
  },
  setEvents(res) {
    if (res.status === HTTP_OK) {
      EventStorge.accessEvents = res.body.events;
      EventStorge.accessEvents = changeTimestamp(EventStorge.accessEvents);
    }
    return res;
  },
};
