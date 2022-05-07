import { httpService } from "./http-service"
import { EventStorge } from "./events-storage";
import { HTTP_OK } from "../utils/http-status";
import { CreateEventRequest } from "../requests/create-event-request";
import { moneyFormatter } from "../utils/money-formatter";
import { INCOME_EVENT_TYPE } from "../utils/event-types";

function changeTimestamp(EventData) {
      for (let event of EventData) {
        let x = new Date(event.timestamp);
        if (x instanceof Date && !isNaN(x)) {
          continue;
        } else {
          event.timestamp = parseDate(event.timestamp);
          event.amount /= 100;
        }
      }
      EventData = sortEvents(EventData);
      return EventData;
    
  }

  function sortEvents(events) {
    return events.sort(compare);
  }

function parseDate(time) {
    var timeArr = time.split(" ");
    var date = timeArr[0].split("-");
    var hour = timeArr[1].split(":");
    var jsDate = new Date(
      date[0],
      parseInt(date[1]) - 1,
      date[2],
      hour[0],
      hour[1],
      hour[2]
    );
    return jsDate;
  }

  function compare(eventA, eventB) {
    let a = new Date(eventA.timestamp);
    let b = new Date(eventB.timestamp);
    if(a < b)
      return -1
    else
      return 1
  }
  

export const eventService = {
    getList() {
        return httpService.get("/events");
    },

    create(formModel) {
        const body = new CreateEventRequest(
            formModel.title,
            moneyFormatter.mapStringToPenniesNumber(
                formModel.eventType == INCOME_EVENT_TYPE ? formModel.amount : -formModel.amount
            ),
            new Date(formModel.date).getTime()
        );
        return httpService.post("/events", body);
    },

    delete(id) {
        return httpService.delete(`/events/${id}`);
    },

    getEventsList() { 
        return httpService.get("/events")
        .then(res => this.setEvents(res))
        .catch(error => console.log(error))
    },
    setEvents(res){
        if (res.status === HTTP_OK) {
            EventStorge.accessEvents = res.body.events;
            EventStorge.accessEvents = changeTimestamp(EventStorge.accessEvents)
        }
        return res;
    }
}

